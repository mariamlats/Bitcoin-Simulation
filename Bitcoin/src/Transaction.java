import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.*;
import java.util.UUID;
public class Transaction {
    // add time variable
    // if its valid or not
    // users need a list of all the transactions they create
    // has the transaction and that will be the transaction id
    // attach the transactionid with the correct user
    private String transactionId; // hash the transaction
    private UUID index; // we can come back to it
    private PublicKey sender;
    public PublicKey recipient;
    private double amount;



    public Transaction(PublicKey sender, PublicKey recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.index = UUID.randomUUID();
        this.transactionId = calculateHash();
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

    public String getTransactionId() {
        return transactionId;
    }

    public UUID getIndex() {
        return index;
    }
}



