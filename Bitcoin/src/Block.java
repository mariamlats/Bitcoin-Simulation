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


