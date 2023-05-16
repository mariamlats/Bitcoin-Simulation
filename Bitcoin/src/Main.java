import java.math.BigInteger;
import java.security.*;
public class Main {
public static void main(String[] args) {
        try {

        User user1 = new User();
        // Generate key pair
        KeyPair keyPair = user1.generateKeyPair();
        user1.setPrivateKey(keyPair.getPrivate());
        user1.setPublicKey(keyPair.getPublic());
        // Generate random value and message

        // Print the keys
        System.out.println("Private Key: " + user1.getPrivateKeyString());
        System.out.println("Public Key: " + (user1.getPublicKeyString()));
        System.out.println(user1.getPublicKeyString());

        Transaction transaction = new Transaction(user1.getPublicKey(), user1.getPublicKey(), 10);
        System.out.println(transaction.getIndex());

        // Generate digital signature
        BigInteger signature = user1.generateDigitalSignature(transaction);
        System.out.println("Digital Signature: " + signature.toString());

        Miner miner = new Miner();
        // Verify digital signature

        boolean isSignatureValid = miner.verifyDigitalSignature(transaction);
        if (isSignatureValid) {
        System.out.println("Digital Signature is valid!");
        } else {
        System.out.println("Digital Signature is invalid.");}


                String inputString = "Hello, world!";
                BigInteger inputInteger1 = new BigInteger("123456789");
                BigInteger inputInteger2 = new BigInteger("987654321");
                BigInteger inputInteger3 = new BigInteger("456789123");
                int zeros = 4;

                BigInteger nonce = miner.mine(inputString, inputInteger1, inputInteger2, zeros);
                if (nonce != null) {
                        System.out.println("Valid nonce found: " + nonce);
                } else {
                        System.out.println("No valid nonce found within the range.");
                }

                System.out.println(miner.calculateHash(inputString + inputInteger1 + inputInteger2 + inputInteger3 + "2495"));



        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidAlgorithmParameterException e) {
        e.printStackTrace();}

        }
}