import java.math.BigDecimal;
import java.security.*;
import java.util.*;
import java.math.BigInteger;


// problems ------------------>
// pair transaction hash and signature
// differentiation problem for a signature and transactionhash in mine function of miner
// balance checker, balance change, block fees


public class Main {
        public static BigInteger previousHash = BigInteger.valueOf(123);
        public static int zeros = 2;

        public static Object[] blockDecoder(Block block) {
                Transaction[] transactions = block.getTransactions();
                int transLength = transactions.length;
                BigInteger[][] sortedTransactionList = new BigInteger[transLength][4]; // Keeps all the transaction data

                for (int i = 0; i < transLength; i++) {
                        Transaction transaction = transactions[i];
                        sortedTransactionList[i][0] = new BigInteger(transaction.getSenderString().replaceAll("\\D", ""));
                        sortedTransactionList[i][1] = new BigInteger(transaction.getRecipientString().replaceAll("\\D", ""));
                        sortedTransactionList[i][2] = BigDecimal.valueOf(transaction.getAmount()).toBigInteger();
                        sortedTransactionList[i][3] = new BigInteger(transaction.getTransactionHash().replaceAll("\\D", ""));

                }
                BigInteger previousHash = (block.getPreviousHash());
                BigInteger currHash = (block.getCurrentHash());

                return new Object[]{sortedTransactionList, previousHash, currHash};     //mere maghla vtvirtav sxvadasxva cvladebad
        }

        ///////////////////////// MAIN //////////////////////////

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

                        //Activates when we recieve a new block every Transaction

 //       Map<BigInteger, BigInteger[][]> TransactionMap = new HashMap<>();  //Map Mapping all the transactionData of a block to its hash is initialized
 //       Map<BigInteger, BigInteger> PrevHashMap = new HashMap<>();    // Map Mapping PreviousHash of a block to its hash is initialized
                        List<List<BigInteger>> BlockChain = new ArrayList<>();       //2Dlist of the BlockChain is intialized
                        Map<BigInteger, Double> Balance = new HashMap<>();   //Tavidanve Ukve datvirtulia

                        int rowLength;    //
                        int maxCounter;  // Just VariablesI'll be using
                        int safetyDifference = 4; //This is the number of how large a block needs to get to be varified

                        // at this point we have a block
                        //we recieve data from a thread

                        Object[] result = blockDecoder(block);
                        BigInteger[][] sortedTransactionList = (BigInteger[][]) result[0]; //The Block Transaction data     //
                        BigInteger prevHash = (BigInteger) result[1];   //Previous Hash of the Block                       // HERE I JUST RECIEVE THE DATA FORM THE BLOCKDECODER AND DISTRIBUTE THE DATA APROPRIATELY
                        BigInteger currHash = (BigInteger) result[2];  // Current Hash of the Block                       //


 //       TransactionMap.put(currHash, sortedTransactionList);   // not much happening
 //       PrevHashMap.put(currHash, prevHash);

                        double transactionAmount;
                        BigInteger transactionSender = BigInteger.ZERO;
                        BigInteger transactionReceiver = BigInteger.ZERO;
                        maxCounter = 0;//this is the variable that keeps the length of the biggest fork
                        for (int i = 0; i < BlockChain.size(); i++) {          //
                                List<BigInteger> innerList = BlockChain.get(i);   // FOR LOOP ITERATING OVER THE BLOCKCHAIN LIST
                                for (int j = 0; j < innerList.size(); j++) {     //

                                        rowLength = BlockChain.get(i).size();      //GETS THE ROW LENGTH OF A SPECIFIC COLUMN

                                        if (maxCounter - rowLength < safetyDifference) {   //IF DIFFERENCE FOR ROWLENGTH IS TOO BIG IT GETS REMOVED
                                                BlockChain.remove(i);                 //

                                                //AFTER REMOVAL "SUPPOSEDLY" ALL OF THIS SHIFTS UP BY ONE

                                                if (Objects.equals(BlockChain.get(i).get(j), block.getCurrentHash())) {  //IF BLOCKCHAIN[I][J] HASH EQUALS THE PREVIOUS HASH OF THE INPUT BLOCK
                                                        for (int k = 0; k <= j; k++) {                                            //AM BLOKAMDE DATA CHAAQVS BOLOSHI QVEMOT
                                                                BlockChain.get(BlockChain.size()).add(k, BlockChain.get(i).get(k));  //
                                                                BlockChain.get(BlockChain.size()).add(k + 1, block.getCurrentHash());  //AXAL FORKS IWYEBS
                                                        }
                                                }
                                        }
                                }
                        }


                        if (BlockChain.size() == 1) {                                                                                        //
                                if (BlockChain.get(0).size() >= safetyDifference) {                                                             //
                                        for (int i = 0; i < sortedTransactionList.length; i++) {   //Fee Isn't Included                            //
                                                //
                                                transactionAmount = sortedTransactionList[i][2].doubleValue();                                       //Once a transaction is confirmed we just update all the balances
                                                transactionSender = sortedTransactionList[i][0];                                                    //And clear the Block
                                                transactionReceiver = sortedTransactionList[i][1];                                                 //
                                                //
                                                Balance.put(transactionSender, Balance.get(transactionSender) - transactionAmount);              //
                                                Balance.put(transactionSender, Balance.get(transactionReceiver) + transactionAmount);

                                        }
                                        BlockChain.clear();
                                }}

                } catch (SignatureException | InvalidKeyException e) {
                        throw new RuntimeException(e);
                }
        }
}
