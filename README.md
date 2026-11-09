# Bitcoin-Inspired Transaction & Mining Demo

A **Java learning project** exploring how digital signatures, transaction hashing and proof-of-work fit together.

The command-line demo creates in-memory identities, signs payment messages, mines three linked blocks and validates their signatures, previous-hash links and proofs. It is a small cryptography and data-integrity exercise, rather than an implementation of the Bitcoin protocol.

## What is implemented

| Concept | Implementation |
| --- | --- |
| Identity | Ephemeral EC key pairs on P-256 / secp256r1 |
| Transaction signatures | ECDSA with SHA-256; raw DER signature bytes are preserved |
| Transaction data | Length-prefixed canonical encoding of sender, recipient, decimal amount and UUID |
| Mining | SHA-256 search over transaction payloads/signatures, previous hash, difficulty and actual nonce |
| Difficulty | 0–4 leading **hexadecimal** zero digits in a 256-bit hash |
| Validation | Verify signatures, reject duplicate transactions within a block, reproduce the proof and check the expected link/difficulty |
| Resource limits | Up to 1,000 transactions per block and 1,000,000 nonce attempts per mining call |

Transaction amounts use BigDecimal, and block arrays/signature bytes are copied at their boundaries. Private keys stay in memory.

## Run

Install a **JDK 17 or newer**, then run from the repository root:

~~~bash
mkdir -p build
javac -d build Bitcoin/src/*.java
java -cp build Main
~~~

The optional argument selects the number of leading hexadecimal zeros:

~~~bash
java -cp build Main 0   # fast walkthrough
java -cp build Main 2   # default
~~~

No external Java libraries, network connection, wallet or API key are required. Each run creates fresh identities and signatures, so hashes and nonces will differ.

Example output shape:

~~~text
Local ECDSA / SHA-256 proof-of-work demo
Difficulty: 2 leading hexadecimal zeros
Block 1 | transactions=2 | nonce=... | hash=00...
Block 2 | transactions=2 | nonce=... | hash=00...
Block 3 | transactions=2 | nonce=... | hash=00...
Validated 3 linked blocks and all transaction signatures.
~~~

If the configured search budget is exhausted, mining stops with an error rather than running indefinitely.

## Regression checks

~~~bash
javac -d build Bitcoin/src/*.java Bitcoin/tests/SimulationTest.java
java -cp build SimulationTest
~~~

The checks exercise valid/forged/malformed signatures, fractional and invalid amounts, defensive copies, unsigned/duplicate transactions, recorded nonce reproduction, difficulty checks, tampered blocks and chain links. They use explicit assertion failures, so JVM assertion flags are unnecessary.

## Scope

This project does not implement balances, a UTXO set, cross-block replay prevention, transaction fees, peer networking, fork resolution, difficulty retargeting or distributed consensus. It uses **P-256**, while Bitcoin uses different protocol and cryptographic conventions. The ECDSA operations sign messages; they are not AES/RSA encryption.

Adding a ledger and network model would be separate extensions requiring their own validation rules and tests.

## Source map

~~~text
Bitcoin/src/
  User.java          Key generation and transaction signing
  Transaction.java   Canonical payload, decimal amount and signature verification
  Miner.java         Bounded nonce search and block hashing
  Block.java         Immutable block snapshot and validation
  Main.java          Three-block command-line walkthrough
Bitcoin/tests/
  SimulationTest.java
~~~

Generated classes and IDE workspace files are excluded from the source tree.
