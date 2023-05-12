import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.UUID;
public class Miner {
    private final UUID id;

    public Miner() {
        //The UUID.randomUUID() function generates a universally unique identifier (UUID) which is highly likely to be unique
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    protected boolean verifyTransaction(Transaction transaction, byte[] signature) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(transaction.getSender());
            sign.update(transaction.getTransactionId().getBytes());
            return sign.verify(signature);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException var4) {
            var4.printStackTrace();
            return false;
        }
    }
}
