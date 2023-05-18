import java.security.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;



// problems ------------------>
// pair transaction hash and signature
// differentiation problem for a signature and transactionhash in mine function of miner
// balance checker, balance change, block fees


// tasks:
// when the block is finished function
// list of transactions from where miner chooses transactions randomly
// and when its added to the ledger the transaction is removed from the list
// it sends the block to the blockchain
// the blockchain checks the hashes and sees if its connected
// if connected it creates a new branch //////////////
// whenever a branch/fork is larger by x then its added to the final ledger
// miners start from the xth one
// SENDER AND RECIPIENT CANT BE THE SAME

public class Main {
        public static String previousHash = "This is a global string.";
        public static int zeros = 2;

        public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {

                Miner miner = new Miner();
                try {
                        Transaction[] transactions = new Transaction[10];
                        User[] users = new User[10];
                        for (int i = 0; i < 10; i++) {
                                User user = new User();
                                KeyPair keyPair = user.generateKeyPair();
                                user.setPrivateKey(keyPair.getPrivate());
                                user.setPublicKey(keyPair.getPublic());
                                users[i] = user;
                        }
                        for (int i = 0; i < 10; i++) {
                                double randomValue = Math.random();
                                User recipient = users[9];
                                transactions[i] = users[i].createTransaction(recipient, randomValue);
                        }

                        Random random = new Random();
                        Set<Integer> selectedIndices = new HashSet<>();
                        Transaction[] selectedTransactions = new Transaction[2];
                        while (selectedIndices.size() < 2) {
                                int randomIndex = random.nextInt(transactions.length);
                                if (!selectedIndices.contains(randomIndex)) {
                                        selectedIndices.add(randomIndex);
                                        selectedTransactions[selectedIndices.size() - 1] = transactions[randomIndex];
                                }
                        }


                        Block block = miner.setTransactions(selectedTransactions);
                        for (Transaction transaction : block.getTransactions()) {
                              System.out.println(transaction.getSender());
                              System.out.println(transaction.getRecipient());
                              System.out.println(transaction.getValidity());

                        }

                } catch (SignatureException | InvalidKeyException e) {
                        throw new RuntimeException(e);
                }
        }
}

