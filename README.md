# Bitcoin‑Simulation

An **educational Python 3** sandbox that lets you **simulate the Bitcoin network**—from peer‑to‑peer block propagation and proof‑of‑work mining to transaction generation and mempool congestion—on your own laptop. Tweak difficulty targets, node counts, or block intervals and watch how the chain evolves in real time.

---

## Why this project?

Understanding Bitcoin’s consensus rules is easier when you can step through them instead of just reading a white‑paper. *Bitcoin‑Simulation* recreates the core mechanics—without touching real money—so you can:

* See how **hash‑based difficulty** keeps blocks \~10 minutes apart.
* Experiment with **51 % attacks** or selfish mining strategies.
* Visualise **forks** and subsequent chain re‑organisations.
* Measure the effect of **network latency** on orphan rates.

Great for classroom demos, blog posts, or your next crypto side quest.

---

## Features

|    | Capability                                                             |
| -- | ---------------------------------------------------------------------- |
| ⛏  | **Proof‑of‑Work** SHA‑256 hashing loop with adjustable difficulty bits |
| 👥 | Multiple **Node** objects connected by a simple gossip network         |
| 📦 | **Block** and **Transaction** classes mirroring Bitcoin Core fields    |
| 🕒 | Real‑time clock or accelerated virtual time │                          |
| 🌐 | Latency & bandwidth throttling to observe fork frequency               |
| 📊 | Matplotlib charts of block times, orphan rates, chain length           |
| 📜 | JSON export of the full chain for offline analysis                     |

---

## Architecture

```
Bitcoin-Simulation/
├─ main.py                 # CLI entry point
├─ node.py                 # Node class: mempool, chain, networking
├─ block.py                # Block + PoW logic
├─ transaction.py          # Minimal TX model (inputs, outputs, fee)
├─ network.py              # Event‑driven message bus (latency, drops)
├─ config.py               # Default parameters + argparse
├─ analysis/
│  └─ plots.py             # Helper functions for visualisations
└─ README.md
```

You’ll see a live log of newly mined blocks, their heights, and which node found them. Afterwards `analysis/block_times.png` opens automatically.

---

## How It Works

1. **Genesis** – `main.py` uses `Block.create_genesis()` to seed each node’s chain.
2. **Event Loop** – Nodes iterate over `mine()` which:

   * Builds a candidate block from random transactions.
   * Increments `nonce` until `block.hash < target`.
3. **Propagation** – `network.broadcast(block)` schedules delivery to peers with random Gaussian latency.
4. **Validation** – Upon receipt, a peer verifies:

   * `prev_hash` matches its current tip.
   * Difficulty meets target.
   * All transactions are valid & unspent.
     Then it sets this block as its tip and gossips it onward.
5. **Fork Handling** – If a peer learns of a **longer chain**, it automatically re‑organises.

Optional real‑time plotting listens to emitted events and streams metrics to `matplotlib`.

---

## Extending the Simulator

* **UTXO set** – Replace the simple balance check with full UTXO tracking.
* **Fee market** – Add variable TX fees and allow miners to choose a mempool subset.
* **Difficulty retarget** – Implement Bitcoin’s 2016‑block adjustment algorithm.
* **Proof‑of‑Stake** – Swap the PoW loop for a PoS slot lottery to compare security models.
* **GUI** – Embed the simulation in a Tk / PyQt dashboard for live visualisation.

---

## Roadmap

* Unit tests for fork resolution and difficulty retargets.
* Dockerfile for reproducible runs.
* CSV export of all blocks for ingestion into Pandas.

