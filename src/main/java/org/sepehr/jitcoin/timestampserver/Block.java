package org.sepehr.jitcoin.timestampserver;

import lombok.Getter;
import lombok.Setter;
import org.sepehr.jitcoin.account.Account;
import org.sepehr.jitcoin.transaction.SimpleTransactionClient;
import org.sepehr.jitcoin.transaction.Transaction;
import org.sepehr.jitcoin.transaction.Utxo;
import org.sepehr.jitcoin.verification.HashManager;
import org.sepehr.jitcoin.verification.MerkleTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Block implements Serializable {

    @Setter
    private int idx;

    @Setter
    private long timestamp;
    private final List<Transaction> items = new ArrayList<>();
    private final byte[] prevHash;
    private long nonce;


    private byte[] transactionRootHash;

    private MerkleTree merkleTree;

    public Block(Account baseAccount, long maxSupply) {
        byte[] genesisPrevTxId = new byte[32];

        Utxo genesisInput = new Utxo(baseAccount.getPublicKey(), maxSupply, 0);
        genesisInput.setTxid(genesisPrevTxId);
        List<Utxo> inputs = List.of(genesisInput);
        inputs.forEach(utxo -> utxo.setConfirmed(true));
        this.prevHash = new byte[32];

        SimpleTransactionClient simpleTransactionManager = new SimpleTransactionClient();

        Transaction transaction = simpleTransactionManager.createTransaction(
                baseAccount.getPublicKey(),
                baseAccount.getPrivateKey(),
                maxSupply,
                baseAccount.getPublicKey(),
                inputs
        );
        transaction.getOut1().setConfirmed(true);
        transaction.getOut0().setConfirmed(true);
        byte[] hash = HashManager.getInstance().hashTransaction(transaction);
        transaction.setHash(hash);
        items.add(transaction);
        this.merkleTree = new MerkleTree(items);
        this.transactionRootHash = merkleTree.getMerkleRoot();
        this.timestamp = System.currentTimeMillis();
    }

    public Block(byte[] prevHash, int idx) {
        this.prevHash = prevHash;
        this.idx = idx;
        this.transactionRootHash = "".getBytes();
    }

    public byte[] getHash() {
        return HashManager.getInstance().hashBlock(this);
    }

    public void appendTransaction(Transaction transaction) {
        items.add(transaction);
        merkleTree = new MerkleTree(items);
        this.transactionRootHash = merkleTree.getMerkleRoot();
    }

    public void increaseNonce() {
        this.nonce++;
    }
}