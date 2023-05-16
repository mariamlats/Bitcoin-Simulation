import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.UUID;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.math.BigInteger;
import java.util.Base64;
public class Miner {
    private final UUID id;

    public Miner() {
        //The UUID.randomUUID() function generates a universally unique identifier (UUID) which is highly likely to be unique
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    protected static boolean verifyDigitalSignature(PublicKey publicKey, BigInteger time, String message, BigInteger signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signatureVerifier = Signature.getInstance("SHA256withECDSA");
        signatureVerifier.initVerify(publicKey);
        signatureVerifier.update(time.toByteArray());
        signatureVerifier.update(message.getBytes());
        return signatureVerifier.verify(signature.toByteArray());
    }


    public static BigInteger mine(String inputString, BigInteger inputInteger1, BigInteger inputInteger2, BigInteger inputInteger3, int zeros) {
        BigInteger startRange = BigInteger.ZERO;
        BigInteger endRange = BigInteger.valueOf(1_000_000_000_000L);

        BigInteger nonce = startRange;

        while (nonce.compareTo(endRange) <= 0) {
            String input = inputString + inputInteger1 + inputInteger2 + inputInteger3 + nonce.toString();
            String hash = calculateHash(input);

            if (hasLeadingZeros(hash, zeros)) {
                return nonce;
            }

            nonce = nonce.add(BigInteger.ONE);
        }

        return null;
    }

    //It uses the SHA-256 algorithm to calculate the hash of the input.
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
