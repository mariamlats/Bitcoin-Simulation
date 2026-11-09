import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/** Local, single-process demonstration; no Bitcoin network or real funds. */
public final class Main {
    public static void main(String[] args) throws GeneralSecurityException {
        int difficulty = 2;
        try {
            if (args.length > 1) throw new IllegalArgumentException();
            if (args.length == 1) difficulty = Integer.parseInt(args[0]);
            if (difficulty < 0 || difficulty > Miner.MAX_DIFFICULTY) throw new IllegalArgumentException();
        } catch (IllegalArgumentException ex) {
            System.err.println("Usage: java -cp build Main [difficulty: 0..4]");
            System.exit(2);
            return;
        }

        User alice = new User();
        User bob = new User();
        User carol = new User();
        Miner miner = new Miner();
        List<Block> chain = new ArrayList<>();
        BigInteger previousHash = BigInteger.ZERO;

        System.out.println("Local ECDSA / SHA-256 proof-of-work demo");
        System.out.println("Difficulty: " + difficulty + " leading hexadecimal zeros");
        for (int height = 1; height <= 3; height++) {
            Transaction[] transactions = {
                alice.createTransaction(bob, new BigDecimal("1.25")),
                bob.createTransaction(carol, new BigDecimal("0.50"))
            };
            Block block = miner.mine(transactions, previousHash, difficulty, Miner.MAX_ATTEMPTS);
            if (!block.isValid(previousHash, difficulty)) {
                throw new IllegalStateException("Candidate block failed validation.");
            }
            chain.add(block);
            System.out.printf("Block %d | transactions=%d | nonce=%s | hash=%064x%n",
                    height, transactions.length, block.getNonce(), block.getCurrentHash());
            previousHash = block.getCurrentHash();
        }

        BigInteger expected = BigInteger.ZERO;
        for (Block block : chain) {
            if (!block.isValid(expected, difficulty)) throw new IllegalStateException("Invalid chain.");
            expected = block.getCurrentHash();
        }
        System.out.println("Validated 3 linked blocks and all transaction signatures.");
        System.out.println("This demo does not implement balances, UTXOs, peer networking or Bitcoin consensus.");
    }
}
