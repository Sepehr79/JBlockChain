# Jitcoin (Java Bitcoin Implementation)

![jitcoin](./jitcoin.png)

A lightweight Java implementation of a decentralized ledger based on the [Bitcoin Whitepaper](https://bitcoin.org/bitcoin.pdf). This project demonstrates core blockchain principles including Proof-of-Work (PoW), Merkle Tree verification, and UTXO-based transactions.

## 📦 Project Structure

The codebase is organized into specialized packages:

*   **`account`**: Cryptographic identity management (`AccountFactory`, `KeyFactory`).
*   **`network`**: Infrastructure for peer communication.
*   **`proofofwork`**: Consensus logic and block mining.
*   **`timestampserver`**: The central ledger logic and block sequencing.
*   **`transaction`**: UTXO model, including transaction inputs and outputs.
*   **`verification`**: Signature validation and Merkle Tree proof logic.

## 🚀 Getting Started

### Prerequisites
* **Java 11+**
* **Maven** (for dependency management and testing)

### Core Logic Example
The following snippet (from `testApplication`) demonstrates the lifecycle of a transaction:

```java
// 1. Initialize Accounts
AccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
Account baseAccount = accountFactory.buildAccount();
Account userA = accountFactory.buildAccount();

// 2. Setup Ledger (TimestampServer)
var timestampServer = new SimpleTimestampServer(baseAccount, 21_000_000);
var client = new SimpleTransactionClient();

// 3. Perform Transaction
var inputs = client.getAccountInputs(timestampServer, baseAccount.getPublicKey());
Transaction tx = client.createTransaction(
        baseAccount.getPublicKey(), 
        baseAccount.getPrivateKey(), 
        100, 
        userA.getPublicKey(), 
        inputs
);

// 4. Secure the Network
timestampServer.appendTransaction(tx);
timestampServer.mineCurrentBlock(Long.MAX_VALUE);

// 5. Verify via Merkle Proof
MerkleTree.TransactionProof proof = timestampServer.getProof(tx);
boolean isValid = client.verifyTransaction(tx, proof);
```

## 🌐 Distributed Networking & Consensus
The `DistributedTimestampServer` allows multiple independent instances to communicate over TCP/IP using a Gossip Protocol.
The following example demonstrates a 3-node network where a transaction propagates through the network and nodes reach consensus after mining.

```java
// 1. Initialize Network (A <-> B <-> C)
SimpleAccountFactory factory = new SimpleAccountFactory(new SimpleKeyFactory());
Account accountA = factory.buildAccount();
Account accountB = factory.buildAccount();

// Create nodes with their respective ports and peer lists
DistributedTimestampServer nodeA = new DistributedTimestampServer(accountA, 1000, 9091, List.of(9092));
DistributedTimestampServer nodeB = new DistributedTimestampServer(accountA, 1000, 9092, List.of(9091, 9093));
DistributedTimestampServer nodeC = new DistributedTimestampServer(accountA, 1000, 9093, List.of(9092));

Thread.sleep(500); // Wait for servers to boot

// 2. Propagation Check
// Create a transaction on Node A
Transaction tx = new SimpleTransactionClient().createTransaction(
        accountA.getPublicKey(), accountA.getPrivateKey(), 10,
        accountB.getPublicKey(), nodeA.getTransactionInputs(accountA.getPublicKey())
);

System.out.println("Step 1: Appending transaction to Node A...");
nodeA.appendTransaction(tx);

Thread.sleep(3000); // Allow time for Gossip protocol to reach Node C

// Verify that Node C received the transaction via Node B
Assertions.assertTrue(nodeC.getCurrentBlock().getItems().contains(tx),
        "Transaction should propagate from Node A to Node C through Node B");

// 3. Mining & Synchronization
System.out.println("Step 3: Node B starts mining...");
boolean mined = nodeB.mineCurrentBlock(5000);
Assertions.assertTrue(mined, "Node B should successfully mine the block");

Thread.sleep(3000); // Allow time for the new block to propagate

// 4. Verification of Consensus
Assertions.assertEquals(nodeB.getCurrentBlockIdx(), nodeA.getCurrentBlockIdx(),
"Node A should sync with Node B's block height");
Assertions.assertArrayEquals(nodeA.getHash(), nodeC.getHash(),
"All nodes must have the exact same last block hash");

System.out.println("Success: Network consensus reached!");
```        

## 🛡️ Security Features

* **Double Spending Prevention**: The TimestampServer explicitly rejects transactions that attempt to reuse inputs.
* **Cryptographic Integrity**: All transactions are signed via private keys and verified using DSA via Java Security.
* **Immutable Ledger**: Once a block is mined via the proofofwork package, transactions are cryptographically linked.
* **Java Serialization**: Uses secure object streams for inter-node communication (Note: Requires classes to implement `Serializable`

## 🧪 Testing
Run the test suite using the Maven Lifecycle:
```bash
mvn test
```
Use code with caution.

## 📜 License
Educational Open Source.
