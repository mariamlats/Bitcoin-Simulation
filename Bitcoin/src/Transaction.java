import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;
public class Transaction {
    // add time variable
    // if its valid or not
    // users need a list of all the transactions they create
    // has the transaction and that will be the transaction id
    // attach the transactionid with the correct user
    protected String transactionHash; // hash the transaction
    private final UUID index; // we can come back to it
    private final PublicKey sender;
    private final PublicKey recipient;
    private final double amount;
    protected BigInteger transactionSignature;
    public boolean validity;

    public Transaction(PublicKey sender, PublicKey recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.validity = false;
        this.index = UUID.randomUUID();
        this.transactionHash = calculateHash();
        this.transactionSignature = null;
    }

    private String calculateHash() {
        String dataToHash = sender.toString() + recipient.toString() + amount + index;
        String hash = null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
            hash = Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return hash;
    }


        public PublicKey getSender() {
            return sender;
        }

        public String getSenderString() {
            return sender.toString();
        }
        public String getRecipientString() {
            return recipient.toString();
        }

        public PublicKey getRecipient() {
            return recipient;
        }

    public double getAmount() {
            return amount;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

    protected void setTransactionSignature(BigInteger transactionSignature) {
            this.transactionSignature = transactionSignature;
        }

        protected BigInteger getTransactionSignature() {
            return transactionSignature;
        }

        public byte[] getIndexBytes() {

            ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
            byteBuffer.putLong(index.getMostSignificantBits());
            byteBuffer.putLong(index.getLeastSignificantBits());

            return byteBuffer.array();
        }

    protected void setValid(boolean isvalid) {
            this.validity = isvalid;
        }

    }


