import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
public class User {

    private PrivateKey privateKey;
    public PublicKey publicKey;

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        keyPairGenerator.initialize(ecSpec, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    public User() {
        try {
            // Generate key pair
            KeyPair keyPair;
            try {
                keyPair = generateKeyPair();
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    protected PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    protected void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    protected void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    protected BigInteger generateDigitalSignature(Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);
        signature.update(transaction.getIndexBytes());
        signature.update(transaction.getTransactionHash().getBytes());
        byte[] signatureBytes = signature.sign();
        return new BigInteger(signatureBytes);
    }

    protected Transaction createTransaction(User recipient, double amount) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Transaction transaction = new Transaction(this.publicKey, recipient.getPublicKey(),amount);
        transaction.setTransactionSignature(generateDigitalSignature(transaction));
        return transaction;
    }


}


