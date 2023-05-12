public class Main {
    public static void main(String[] args) {
        User user1 = new User();
        User user2 = new User();
        Miner miner = new Miner();
        Transaction transaction = new Transaction(user1.publicKey, user2.publicKey, 10.0, "1");
        byte[] signature = user1.signTransaction(transaction.getTransactionId());
        System.out.println(signature.getClass());

// Later, when verifying the transaction:
        if (miner.verifyTransaction(transaction, signature)) {
            System.out.println("Transaction signature is valid!");
        } else {
            System.out.println("Transaction signature is invalid.");
        }


    }
}
