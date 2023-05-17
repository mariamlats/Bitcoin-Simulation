import java.math.BigInteger;

public class Block {
    public String previousHash;
    public String currentHash;
    public Transaction[] transactions;
    public BigInteger nonce;


    public Block(String previousHash, String  currentHash, Transaction[] transactions,  BigInteger nonce) {
        this.previousHash = previousHash;
        this.currentHash = currentHash;
        this.transactions = transactions;
        this.nonce = nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public BigInteger getNonce() {
        return nonce;
    }
}


// when the block is finished function
// it sends the block to the blockchain
// the blockchain checks the hashes and sees if its connected
// if connected it creates a new branch //////////////
// whenever a branch/fork is larger by x then its added to the final ledger
// miners start from the xth one
