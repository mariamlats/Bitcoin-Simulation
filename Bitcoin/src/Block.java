import java.math.BigInteger;
import java.util.Objects;

/** A snapshot of a candidate block; construction alone does not validate a proof. */
public final class Block {
    private final BigInteger previousHash;
    private final BigInteger currentHash;
    private final Transaction[] transactions;
    private final BigInteger nonce;
    private final int difficulty;

    public Block(BigInteger previousHash, BigInteger currentHash, Transaction[] transactions,
                 BigInteger nonce, int difficulty) {
        this.previousHash = Objects.requireNonNull(previousHash);
        this.currentHash = Objects.requireNonNull(currentHash);
        this.transactions = Objects.requireNonNull(transactions).clone();
        this.nonce = Objects.requireNonNull(nonce);
        this.difficulty = difficulty;
    }

    public boolean isValid(BigInteger expectedPreviousHash, int expectedDifficulty) {
        try {
            if (!previousHash.equals(expectedPreviousHash) || difficulty != expectedDifficulty) return false;
            Miner.validateTransactions(transactions);
            return Miner.hasLeadingZeros(currentHash, difficulty)
                    && currentHash.equals(Miner.calculateHash(transactions, previousHash, nonce, difficulty));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public BigInteger getPreviousHash() { return previousHash; }
    public BigInteger getCurrentHash() { return currentHash; }
    public Transaction[] getTransactions() { return transactions.clone(); }
    public BigInteger getNonce() { return nonce; }
    public int getDifficulty() { return difficulty; }
}
