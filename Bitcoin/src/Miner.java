import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Bounded proof-of-work over signed transactions, previous hash and actual nonce. */
public final class Miner {
    public static final int MAX_DIFFICULTY = 4;
    public static final long MAX_ATTEMPTS = 1_000_000L;
    private final UUID id = UUID.randomUUID();

    public UUID getId() { return id; }

    public Block mine(Transaction[] transactions, BigInteger previousHash, int difficulty, long maxAttempts) {
        validateParameters(previousHash, difficulty);
        if (maxAttempts <= 0 || maxAttempts > MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Attempts must be between 1 and " + MAX_ATTEMPTS + ".");
        }
        Transaction[] snapshot = transactions == null ? null : transactions.clone();
        validateTransactions(snapshot);
        byte[] prefix = encodePrefix(snapshot, previousHash, difficulty);
        for (long attempt = 0; attempt < maxAttempts; attempt++) {
            if (Thread.currentThread().isInterrupted()) {
                throw new IllegalStateException("Mining interrupted.");
            }
            BigInteger nonce = BigInteger.valueOf(attempt);
            BigInteger hash = hash(prefix, nonce);
            if (hasLeadingZeros(hash, difficulty)) {
                return new Block(previousHash, hash, snapshot, nonce, difficulty);
            }
        }
        throw new IllegalStateException("No proof found within the configured attempt limit.");
    }

    static void validateParameters(BigInteger previousHash, int difficulty) {
        if (previousHash == null || previousHash.signum() < 0 || previousHash.bitLength() > 256) {
            throw new IllegalArgumentException("Previous hash must be an unsigned 256-bit value.");
        }
        if (difficulty < 0 || difficulty > MAX_DIFFICULTY) {
            throw new IllegalArgumentException("Difficulty must be between 0 and " + MAX_DIFFICULTY + " hexadecimal zeros.");
        }
    }

    static void validateTransactions(Transaction[] transactions) {
        if (transactions == null || transactions.length == 0 || transactions.length > 1000) {
            throw new IllegalArgumentException("A block needs between 1 and 1000 transactions.");
        }
        Set<String> seen = new HashSet<>();
        for (Transaction transaction : transactions) {
            if (transaction == null || !transaction.isSignatureValid()
                    || !seen.add(transaction.getTransactionHash())) {
                throw new IllegalArgumentException("Transactions must be signed, valid and unique within a block.");
            }
        }
    }

    private static byte[] encodePrefix(Transaction[] transactions, BigInteger previousHash, int difficulty) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(buffer);
            data.writeUTF("block-v1");
            Transaction.writeBytes(data, previousHash.toByteArray());
            data.writeInt(difficulty);
            data.writeInt(transactions.length);
            for (Transaction transaction : transactions) {
                Transaction.writeBytes(data, transaction.getPayload());
                Transaction.writeBytes(data, transaction.getTransactionSignature());
            }
            return buffer.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static BigInteger calculateHash(Transaction[] transactions, BigInteger previousHash,
                                           BigInteger nonce, int difficulty) {
        validateParameters(previousHash, difficulty);
        if (nonce == null || nonce.signum() < 0 || nonce.bitLength() > 63) {
            throw new IllegalArgumentException("Nonce must be a non-negative long.");
        }
        return hash(encodePrefix(transactions, previousHash, difficulty), nonce);
    }

    private static BigInteger hash(byte[] prefix, BigInteger nonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(prefix);
            // A fixed-width long makes nonce boundaries explicit and reproducible.
            digest.update(java.nio.ByteBuffer.allocate(Long.BYTES).putLong(nonce.longValueExact()).array());
            return new BigInteger(1, digest.digest());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable.", ex);
        }
    }

    public static boolean hasLeadingZeros(BigInteger hash, int difficulty) {
        if (hash == null || hash.signum() < 0 || hash.bitLength() > 256
                || difficulty < 0 || difficulty > MAX_DIFFICULTY) return false;
        return hash.bitLength() <= 256 - 4 * difficulty;
    }
}
