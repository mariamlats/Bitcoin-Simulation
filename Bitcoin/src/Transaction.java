import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.*;

public class Transaction {
    private String transactionId;
    private String index;
    private PublicKey sender;
    private PublicKey recipient;
    private double amount;

    public Transaction(PublicKey sender, PublicKey recipient, double amount, String index) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.index = index;
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
}


