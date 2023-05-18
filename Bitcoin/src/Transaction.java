import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
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
    private UUID index; // we can come back to it
    private PublicKey sender;
    private PublicKey recipient;
    private double amount;
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
        String dataToHash = sender.toString() + recipient.toString() + Double.toString(amount) + index;
        String hash = null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(dataToHash.getBytes("UTF-8"));
            hash = Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        return hash;
    }

    public PublicKey getSender() {
        return sender;
    }
    public PublicKey getRecipient() {
        return recipient;
    }
    public Boolean getValidity(){return validity;}
    public double getAmount() {
        return amount;
    }
    public String getTransactionHash() {
        return transactionHash;
    }
    public UUID getIndex() {
        return index;
    }
    protected void setTransactionSignature(BigInteger transactionSignature) {
        this.transactionSignature = transactionSignature;
    }
    protected BigInteger getTransactionSignature() {return transactionSignature;}
    public byte[] getIndexBytes(){

        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(index.getMostSignificantBits());
        byteBuffer.putLong(index.getLeastSignificantBits());

        return byteBuffer.array();
    }

    protected void setSender(PublicKey publicKey) {
        this.sender = publicKey;
    }
    protected void setValid (boolean isvalid){this.validity = isvalid;}

}


