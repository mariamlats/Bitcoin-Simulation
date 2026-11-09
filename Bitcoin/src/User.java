import java.math.BigDecimal;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Objects;

/** An in-memory P-256 identity. Private keys are neither exposed nor persisted. */
public final class User {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public User() {
        try {
            KeyPair pair = generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Could not initialise an EC identity.", ex);
        }
    }

    public static KeyPair generateKeyPair() throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
        return generator.generateKeyPair();
    }

    public PublicKey getPublicKey() { return publicKey; }

    byte[] sign(Transaction transaction) throws GeneralSecurityException {
        if (!Arrays.equals(publicKey.getEncoded(), transaction.getSender().getEncoded())) {
            throw new IllegalArgumentException("An identity may sign only its own transactions.");
        }
        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(privateKey);
        signer.update(transaction.getPayload());
        return signer.sign();
    }

    public Transaction createTransaction(User recipient, BigDecimal amount) throws GeneralSecurityException {
        Objects.requireNonNull(recipient, "recipient");
        Transaction transaction = new Transaction(publicKey, recipient.publicKey, amount);
        transaction.attachSignature(sign(transaction));
        return transaction;
    }

    public Transaction createTransaction(User recipient, double amount) throws GeneralSecurityException {
        Transaction transaction = new Transaction(publicKey, Objects.requireNonNull(recipient).publicKey, amount);
        transaction.attachSignature(sign(transaction));
        return transaction;
    }
}
