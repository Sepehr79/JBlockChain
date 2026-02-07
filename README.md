# Blockchain Implementation

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

## 🛡️ Security Features

* **Double Spending Prevention**: The TimestampServer explicitly rejects transactions that attempt to reuse inputs.

* **Cryptographic Integrity**: All transactions are signed via private keys and verified using ECDSA or RSA via Java Security.

* **Immutable Ledger**: Once a block is mined via the proofofwork package, transactions are cryptographically linked.

## 🧪 Testing
Run the test suite using the Maven Lifecycle:
```bash
mvn test
```
Use code with caution.

## 📜 License
Educational Open Source.
