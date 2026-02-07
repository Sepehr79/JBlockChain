package org.sepehr.jblockchain.timestampserver;

import lombok.Getter;
import lombok.Setter;
import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.transaction.SimpleTransactionClient;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;
import org.sepehr.jblockchain.verification.HashManager;
import org.sepehr.jblockchain.verification.MerkleTree;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Block {

    @Setter
    private int idx;
    private final List<Transaction> items = new ArrayList<>();
    private final byte[] prevHash;
    private long nonce;

    private byte[] transactionRootHash;

    private MerkleTree merkleTree;

    public Block(Account baseAccount, long maxSupply) {
        List<Utxo> inputs = List.of(new Utxo(baseAccount.getPublicKey(), maxSupply, "".getBytes(), 0));
        inputs.forEach(utxo -> utxo.setConfirmed(true));
        this.prevHash = "".getBytes();
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
        byte[] hash = HashManager.getInstance().hashTransaction(transaction, inputs);
        transaction.setHash(hash);
        items.add(transaction);
        this.merkleTree = new MerkleTree(items);
        this.transactionRootHash = merkleTree.getMerkleRoot();
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