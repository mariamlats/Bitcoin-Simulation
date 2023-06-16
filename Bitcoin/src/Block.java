import java.math.BigInteger;

public class Block {
    public BigInteger previousHash;
    public BigInteger currentHash;
    public Transaction[] transactions;
    public BigInteger nonce;


    public Block(BigInteger previousHash, BigInteger  currentHash, Transaction[] transactions,  BigInteger nonce) {
        this.previousHash = previousHash;
        this.currentHash = currentHash;
        this.transactions = transactions;
        this.nonce = nonce;
    }

    public BigInteger getPreviousHash() {
        return previousHash;
    }

    public BigInteger getCurrentHash() {
        return currentHash;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public BigInteger getNonce() {
        return nonce;
    }
}


