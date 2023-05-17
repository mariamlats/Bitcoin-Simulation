import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.UUID;
public class Miner {
    private final UUID id;

    public Miner() {
        //The UUID.randomUUID() function generates a universally unique identifier (UUID) which is highly likely to be unique
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    protected static boolean verifyDigitalSignature(Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signatureVerifier = Signature.getInstance("SHA256withECDSA");
        signatureVerifier.initVerify(transaction.getSender());
        signatureVerifier.update(transaction.getIndexBytes());
        signatureVerifier.update(transaction.getTransactionHash().getBytes());
        return signatureVerifier.verify(transaction.getTransactionSignature().toByteArray());
    }

    //transaction = transactionsignature  unda wamovides transaction sheikras da mere gatardes hashshi
    // list sheikmnas transactiebis da egre shemovides
    // pair transaction hash and signature
    // list of transactions
    public static BigInteger nonceFinder(Transaction[] transactions, String previousHash, BigInteger nonceInt, int zeros) {
        BigInteger startRange = BigInteger.ZERO;
        BigInteger endRange = BigInteger.valueOf(1_000_000_000_000L);

        BigInteger nonce = startRange;
        String data = "";
        String hash = null;
        while (nonce.compareTo(endRange) <= 0) {
            for (int i = 0; i < transactions.length; i++) {
                // differentiation problem for a signature and transactionhash
                data += transactions[i].getTransactionSignature() + " " + transactions[i].getTransactionHash().toString() + " ";
            }
            hash = calculateHash(data, previousHash, nonceInt, zeros);

            if (hasLeadingZeros(hash, zeros)) {
                return nonce;
            }

            nonce = nonce.add(BigInteger.ONE);

        }

        return null;
    }

    private static String calculateHash(String data, String previousHash, BigInteger inputInteger2, int zeros) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(data.getBytes(StandardCharsets.UTF_8));
            digest.update(previousHash.getBytes(StandardCharsets.UTF_8));
            digest.update(inputInteger2.toByteArray());
            byte[] zerosByteArray = ByteBuffer.allocate(4).putInt(zeros).array();
            digest.update(zerosByteArray);
            byte[] encodedHash = digest.digest();
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    //  method, which counts the number of leading zeros in the hash.
    //  If the count is equal to or greater than the specified zeros,
    //  the mining process is considered successful, and the current nonce is returned.
    //If no matching hash is found within the defined range, the mine method returns null
    // to indicate that the mining process was unsuccessful.

    private static boolean hasLeadingZeros(String hash, int zeros) {
        int count = 0;
        for (int i = 0; i < hash.length(); i++) {
            if (hash.charAt(i) != '0') {
                break;
            }
            count++;
        }
        return count >= zeros;
    }

    protected static Block createBlock (String previousHash, String  currentHash, Transaction[] transactions,  BigInteger nonce){
        Block block = new Block(previousHash, currentHash, transactions, nonce);
        return block;
    }

    protected Block mineBlock(Transaction[] transactions, String previousHash, BigInteger nonceInt, int zeros){
        BigInteger nonce = nonceFinder(transactions,previousHash,nonceInt,zeros);
        String data = "";
        String hash = null;
        for (int i = 0; i < transactions.length; i++) {
            data += transactions[i].getTransactionSignature() + " " + transactions[i].getTransactionHash().toString() + " ";
        }
         hash = calculateHash(data, previousHash, nonceInt, zeros);
        Block block = createBlock(previousHash, hash, transactions, nonce);

        return block;
    }

}

