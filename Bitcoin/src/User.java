import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class User {
    private PrivateKey privateKey;
    public PublicKey publicKey;

    public User() {
        try {
            // Generate key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Set private and public keys
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    protected byte[] signTransaction(String transactionHash) {
        byte[] signature = null;
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            PrivateKey privatekey = this.getPrivateKey();
            sign.initSign(privateKey); //takes private key
            sign.update(transactionHash.getBytes()); //In the case of the code you provided, transactionHash.getBytes() is passed as an argument to sign.update(), which means that the message digest is updated with the bytes of the transaction hash.
            signature = sign.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            ex.printStackTrace();
        }
        return signature;
    }

    protected boolean verifyTransaction(Transaction transaction, byte[] signature) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(transaction.getSender()); //takes public key as an input
            sign.update(transaction.getTransactionId().getBytes());
            return sign.verify(signature); // pass raw binary signature
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}

