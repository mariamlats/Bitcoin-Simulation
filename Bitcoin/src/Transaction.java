import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/** A signed payment message for the local teaching simulation. */
public final class Transaction {
    private final UUID index = UUID.randomUUID();
    private final PublicKey sender;
    private final PublicKey recipient;
    private final BigDecimal amount;
    private final byte[] payload;
    private final String transactionHash;
    private byte[] signature;

    public Transaction(PublicKey sender, PublicKey recipient, double amount) {
        this(sender, recipient, finiteAmount(amount));
    }

    public Transaction(PublicKey sender, PublicKey recipient, BigDecimal amount) {
        this.sender = Objects.requireNonNull(sender, "sender");
        this.recipient = Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() <= 0 || amount.precision() > 30 || Math.abs((long) amount.scale()) > 18) {
            throw new IllegalArgumentException("Amount must be positive and fit the demo's numeric limits.");
        }
        this.amount = amount.stripTrailingZeros();
        this.payload = encode();
        try {
            this.transactionHash = Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(payload));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable.", ex);
        }
    }

    private static BigDecimal finiteAmount(double amount) {
        if (!Double.isFinite(amount)) {
            throw new IllegalArgumentException("Amount must be finite.");
        }
        return BigDecimal.valueOf(amount);
    }

    private byte[] encode() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(buffer);
            data.writeUTF("transaction-v1");
            writeBytes(data, sender.getEncoded());
            writeBytes(data, recipient.getEncoded());
            writeBytes(data, amount.toPlainString().getBytes(StandardCharsets.UTF_8));
            data.writeLong(index.getMostSignificantBits());
            data.writeLong(index.getLeastSignificantBits());
            return buffer.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void writeBytes(DataOutputStream data, byte[] bytes) throws IOException {
        data.writeInt(bytes.length);
        data.write(bytes);
    }

    void attachSignature(byte[] signature) {
        if (this.signature != null || signature == null || signature.length == 0 || signature.length > 128) {
            throw new IllegalArgumentException("A transaction accepts one bounded ECDSA signature.");
        }
        this.signature = signature.clone();
    }

    public boolean isSignatureValid() {
        if (signature == null) return false;
        try {
            Signature verifier = Signature.getInstance("SHA256withECDSA");
            verifier.initVerify(sender);
            verifier.update(payload);
            return verifier.verify(signature);
        } catch (GeneralSecurityException ex) {
            return false;
        }
    }

    public PublicKey getSender() { return sender; }
    public PublicKey getRecipient() { return recipient; }
    public BigDecimal getAmount() { return amount; }
    public String getTransactionHash() { return transactionHash; }
    public byte[] getPayload() { return payload.clone(); }
    public byte[] getTransactionSignature() { return signature == null ? null : signature.clone(); }
}
