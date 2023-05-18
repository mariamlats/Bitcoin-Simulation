import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.UUID;
public class Miner {
    private final UUID id;
    public Transaction[] transactions;
    public Miner() {
        //The UUID.randomUUID() function generates a universally unique identifier (UUID) which is highly likely to be unique
        this.id = UUID.randomUUID();
        this.transactions = new Transaction[4];
    }

    public UUID getId() {
        return id;
    }

    protected Block setTransactions(Transaction[] transactions) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        this.transactions = transactions;
        // once the transactions are chosen, perform verify transactions
        for (Transaction transaction : transactions) {
            boolean isSignatureValid = this.verifyDigitalSignature(transaction);
            transaction.setValid(isSignatureValid);
        }
        // HOW WILL THIS WORK ???????????????????????????????????????????????????????????????????????????
        BigInteger nonceInt = new BigInteger("456789123");
        Object[] values = nonceFinder(transactions, Main.previousHash,nonceInt );
        String currenthash = (String) values[0];
        BigInteger nonce = (BigInteger) values[1];
        Block block = new Block(Main.previousHash, currenthash, transactions, nonce);
        return block;
        }

    //transaction = transactionsignature  unda wamovides transaction sheikras da mere gatardes hashshi
    // list sheikmnas transactiebis da egre shemovides
    // pair transaction hash and signature

    protected  boolean verifyDigitalSignature(Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signatureVerifier = Signature.getInstance("SHA256withECDSA");
        signatureVerifier.initVerify(transaction.getSender());
        signatureVerifier.update(transaction.getIndexBytes());
        signatureVerifier.update(transaction.getTransactionHash().getBytes());
        return signatureVerifier.verify(transaction.getTransactionSignature().toByteArray());
    }


    public static Object[] nonceFinder(Transaction[] transactions, String previousHash, BigInteger nonceInt) {
        BigInteger startRange = BigInteger.ZERO;
        BigInteger endRange = BigInteger.valueOf(1_000_000_000_000L);
        BigInteger nonce = startRange;
        String data = "";
        String hash = null;
        while (nonce.compareTo(endRange) <= 0) {
            for (int i = 0; i < transactions.length; i++) {
                // differentiation problem for a signature and transactionhash
                data += transactions[i].getTransactionSignature() + " " + transactions[i].getTransactionHash() + " ";
            }
            hash = calculateHash(data, previousHash, nonceInt);

            if (hasLeadingZeros(hash, Main.zeros)) {
                return new Object[] { hash, nonce };

            }

            nonce = nonce.add(BigInteger.ONE);

        }

        return null;
    }

    private static String calculateHash(String data, String previousHash, BigInteger inputInteger2) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(data.getBytes(StandardCharsets.UTF_8));
            digest.update(previousHash.getBytes(StandardCharsets.UTF_8));
            digest.update(inputInteger2.toByteArray());
            byte[] zerosByteArray = ByteBuffer.allocate(4).putInt(Main.zeros).array();
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

}

