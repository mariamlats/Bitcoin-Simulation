import java.math.BigDecimal;
import java.math.BigInteger;

/** Dependency-free regression checks; failures throw AssertionError. */
public final class SimulationTest {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static void rejects(Runnable operation) {
        try { operation.run(); }
        catch (IllegalArgumentException | IllegalStateException expected) { checks++; return; }
        throw new AssertionError("Invalid input was accepted");
    }
    public static void main(String[] args) throws Exception {
        User alice = new User(), bob = new User();
        Transaction valid = alice.createTransaction(bob, new BigDecimal("1.25"));
        check(valid.isSignatureValid(), "Valid signature rejected");
        check(valid.getAmount().equals(new BigDecimal("1.25")), "Fractional amount lost");
        byte[] signature = valid.getTransactionSignature();
        signature[0] ^= 1;
        byte[] payload = valid.getPayload();
        payload[0] ^= 1;
        check(valid.isSignatureValid(), "Mutable signature/payload getter");
        Transaction unsigned = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 1);
        check(!unsigned.isSignatureValid(), "Unsigned transaction accepted");
        Transaction forged = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 9);
        forged.attachSignature(valid.getTransactionSignature());
        check(!forged.isSignatureValid(), "Signature valid for different payload");
        Transaction malformed = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 1);
        malformed.attachSignature(new byte[]{1, 2, 3});
        check(!malformed.isSignatureValid(), "Malformed signature accepted");
        rejects(() -> valid.attachSignature(new byte[]{1}));
        rejects(() -> new Transaction(alice.getPublicKey(), bob.getPublicKey(), -1));
        rejects(() -> new Transaction(alice.getPublicKey(), bob.getPublicKey(), 0));
        rejects(() -> new Transaction(alice.getPublicKey(), bob.getPublicKey(), Double.NaN));
        rejects(() -> new Transaction(alice.getPublicKey(), bob.getPublicKey(), Double.POSITIVE_INFINITY));
        rejects(() -> new Transaction(alice.getPublicKey(), bob.getPublicKey(), new BigDecimal("1E-1000")));

        Miner miner = new Miner();
        Transaction[] transactions = {valid};
        Block first = miner.mine(transactions, BigInteger.ZERO, 2, Miner.MAX_ATTEMPTS);
        check(first.isValid(BigInteger.ZERO, 2), "Mined proof invalid");
        check(String.format("%064x", first.getCurrentHash()).startsWith("00"), "Wrong proof target");
        check(first.getCurrentHash().equals(Miner.calculateHash(transactions, BigInteger.ZERO, first.getNonce(), 2)),
                "Recorded nonce does not reproduce proof");
        check(!first.getCurrentHash().equals(Miner.calculateHash(transactions, BigInteger.ZERO,
                first.getNonce().add(BigInteger.ONE), 2)), "Nonce ignored");
        check(!first.isValid(BigInteger.ONE, 2), "Wrong previous hash accepted");
        check(!first.isValid(BigInteger.ZERO, 0), "Wrong difficulty accepted");
        transactions[0] = unsigned;
        first.getTransactions()[0] = unsigned;
        check(first.isValid(BigInteger.ZERO, 2), "Mutable block array");
        Block altered = new Block(first.getPreviousHash(), first.getCurrentHash(),
                first.getTransactions(), first.getNonce().add(BigInteger.ONE), 2);
        check(!altered.isValid(BigInteger.ZERO, 2), "Tampered nonce accepted");
        Block second = miner.mine(new Transaction[]{bob.createTransaction(alice, 0.5)},
                first.getCurrentHash(), 1, Miner.MAX_ATTEMPTS);
        check(second.isValid(first.getCurrentHash(), 1), "Linked block rejected");

        rejects(() -> miner.mine(new Transaction[]{unsigned}, BigInteger.ZERO, 0, 1));
        rejects(() -> miner.mine(new Transaction[]{forged}, BigInteger.ZERO, 0, 1));
        rejects(() -> miner.mine(new Transaction[]{valid, valid}, BigInteger.ZERO, 0, 1));
        rejects(() -> miner.mine(new Transaction[0], BigInteger.ZERO, 0, 1));
        rejects(() -> miner.mine(new Transaction[]{null}, BigInteger.ZERO, 0, 1));
        rejects(() -> miner.mine(new Transaction[]{valid}, BigInteger.ZERO, -1, 1));
        rejects(() -> miner.mine(new Transaction[]{valid}, BigInteger.ZERO, 5, 1));
        rejects(() -> miner.mine(new Transaction[]{valid}, BigInteger.ZERO, 1, 0));
        rejects(() -> miner.mine(new Transaction[]{valid}, BigInteger.ZERO, 1, Miner.MAX_ATTEMPTS + 1));
        check(!Miner.hasLeadingZeros(BigInteger.ONE.shiftLeft(255), 2), "Decimal zero count used");
        check(!Miner.hasLeadingZeros(BigInteger.ONE.shiftLeft(256), 0), "Oversized hash accepted");
        check(miner.mine(new Transaction[]{valid}, BigInteger.ZERO, 0, 1).getNonce().equals(BigInteger.ZERO),
                "Difficulty zero must accept first nonce");
        System.out.println("PASS: " + checks + " signature, transaction, mining and block-integrity checks.");
    }
}
