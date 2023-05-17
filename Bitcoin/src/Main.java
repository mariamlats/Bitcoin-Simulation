import java.math.BigInteger;
import java.security.*;

// problems ------------------>
//transaction = transactionsignature  unda wamovides transaction sheikras da mere gatardes hashshi
// list sheikmnas transactiebis da egre shemovides
// pair transaction hash and signature
// differentiation problem for a signature and transactionhash in mine function of miner
// balance checker, balance change, block fees

public class Main {
public static void main(String[] args) {
        try {
        Miner miner = new Miner();
        User user1 = new User();
        User user2 = new User();
        // Generate key pair
        KeyPair keyPair1 = user1.generateKeyPair();
        user1.setPrivateKey(keyPair1.getPrivate());
        user1.setPublicKey(keyPair1.getPublic());
        KeyPair keyPair2 = user2.generateKeyPair();
        user2.setPrivateKey(keyPair2.getPrivate());
        user2.setPublicKey(keyPair2.getPublic());


        Transaction transaction1 = user1.createTransaction(user2,10);
        Transaction transaction2 = user2.createTransaction(user1,15);
        //System.out.println(transaction1.getTransactionSignature());
        //transaction.getIndex(), transaction.getTransactionSignature(), transaction.getTransactionHash());


 //       BigInteger signature = user1.generateDigitalSignature(transaction);
 //       System.out.println("Digital Signature: " + signature.toString());


        // Verify digital signature
        // we changed the sender so it would become invalid
        //transaction1.setSender(user2.getPublicKey());
        boolean isSignatureValid = miner.verifyDigitalSignature(transaction1);
        if (isSignatureValid) {
        System.out.println("Digital Signature is valid!");
        } else {
        System.out.println("Digital Signature is invalid.");}
        Transaction[] transactionlist = new Transaction[2];
        transactionlist[0] = transaction1;
        transactionlist[1] = transaction2;

        String previoushash = "1";
        BigInteger nonceInt = new BigInteger("456789123");
        int zeros = 2;
        BigInteger nonce= miner.mine(transactionlist,previoushash, nonceInt, zeros);

        if (nonce != null) {
                System.out.println("Valid nonce found: " + nonce);
        } else {
                System.out.println("No valid nonce found within the range.");
        }

             //   System.out.println(miner.calculateHash(inputString + inputInteger1 + inputInteger2 + inputInteger3 + "2495"));


        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidAlgorithmParameterException e) {
        e.printStackTrace();}

        }
}