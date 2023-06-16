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
        BigInteger nonceInt = new BigInteger("123");
        Object[] values = nonceFinder(transactions, Main.previousHash,nonceInt );
        BigInteger currenthash = (BigInteger) values[0];
        BigInteger nonce = (BigInteger) values[1];

        return new Block(Main.previousHash, currenthash, transactions, nonce);
        }


    protected  boolean verifyDigitalSignature(Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signatureVerifier = Signature.getInstance("SHA256withECDSA");
        signatureVerifier.initVerify(transaction.getSender());
        signatureVerifier.update(transaction.getIndexBytes());
        signatureVerifier.update(transaction.getTransactionHash().getBytes());
        return signatureVerifier.verify(transaction.getTransactionSignature().toByteArray());
    }


    public static Object[] nonceFinder(Transaction[] transactions, BigInteger previousHash, BigInteger nonceInt) {
        BigInteger startRange = BigInteger.ZERO;
        BigInteger endRange = BigInteger.valueOf(1_000_000_000_000L);
        BigInteger nonce = startRange;
        String data = "";
        BigInteger hash = null;


        while (nonce.compareTo(endRange) <= 0) {
            for (int i = 0; i < transactions.length; i++) {
                // differentiation problem for a signature and transactionhash
                data += transactions[i].getTransactionSignature().toString() + " " + transactions[i].getTransactionHash() + " ";
            }

            hash = calculateHash(data, previousHash, nonceInt);

            if (hasLeadingZeros(String.valueOf(hash), Main.zeros)) {
                return new Object[] { hash, nonce };
            }
            nonce = nonce.add(BigInteger.ONE);
        }
        return null;
    }

    private static BigInteger calculateHash(String data, BigInteger previousHash, BigInteger inputInteger2) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(data.getBytes(StandardCharsets.UTF_8));
            digest.update(previousHash.toByteArray());
            digest.update(inputInteger2.toByteArray());
            byte[] zerosByteArray = ByteBuffer.allocate(4).putInt(Main.zeros).array();
            digest.update(zerosByteArray);
            return new BigInteger(1,digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    //  method, which counts the number of leading zeros in the hash.
    //  If the count is equal to or greater than the specified zeros,
    //  the mining process is considered successful, and the current nonce is returned.
    //If no matching hash is found within the defined range, the mine method returns null
    // to indicate that the mining process was unsuccessful.

    private static boolean hasLeadingZeros(String hash, int zeros) {
        int count = 0;
        for (int i = 0; i < hash.length(); i++) {
            if (hash.charAt(i) == '0') {
                count++;}
            if (count >= zeros) {
                return true;  // Return true if the required number of leading zeros is reached
            }
        }
        return false;  // Return false if the required number of leading zeros is not reached
    }

}

