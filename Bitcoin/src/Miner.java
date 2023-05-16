import java.math.BigInteger;
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


    public static BigInteger mine(String transaction, BigInteger previousHash, BigInteger nonceInt, int zeros) {
        BigInteger startRange = BigInteger.ZERO;
        BigInteger endRange = BigInteger.valueOf(1_000_000_000_000L);

        BigInteger nonce = startRange;

        while (nonce.compareTo(endRange) <= 0) {
            String input = transaction + previousHash + nonceInt + nonce.toString();
            String hash = calculateHash(input);

            if (hasLeadingZeros(hash, zeros)) {
                return nonce;
            }

            nonce = nonce.add(BigInteger.ONE);
        }

        return null;
    }

    //It uses the SHA-256 algorithm to calculate the hash of the input.
    // is this transaction hash or block hash???????????????????????????
    protected static String calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // what hash is this as an input???????????????????
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
    // inputs????????????????????????????????????????????????
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
