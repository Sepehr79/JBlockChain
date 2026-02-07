package org.sepehr.jblockchain.timestampserver;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
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

    private String rootHash;

    private MerkleTree merkleTree;

    public Block(Account baseAccount, int idx) {
        this.idx = idx;
        List<Utxo> inputs = List.of(new Utxo(baseAccount.getPublicKey(), 21_000_000, "".getBytes(), 0));
        inputs.forEach(utxo -> utxo.setConfirmed(true));
        this.prevHash = "".getBytes();
        SimpleTransactionClient simpleTransactionManager = new SimpleTransactionClient();
        Transaction transaction = simpleTransactionManager.createTransaction(
                baseAccount.getPublicKey(),
                baseAccount.getPrivateKey(),
                21_000_000,
                baseAccount.getPublicKey(),
                inputs
        );
        transaction.getOut1().setConfirmed(true);
        transaction.getOut0().setConfirmed(true);
        byte[] hash = HashManager.hashTransaction(transaction, inputs);
        transaction.setHash(hash);
        items.add(transaction);
        this.rootHash = "";
    }

    public Block(byte[] prevHash, int idx) {
        this.prevHash = prevHash;
        this.idx = idx;
        this.rootHash = "";
    }

    public byte[] getHash() {
        return HashManager.hashBlock(this);
    }

    public void appendTransaction(Transaction transaction) {
        items.add(transaction);
        merkleTree = new MerkleTree(items);
        this.rootHash = merkleTree.getMerkleRoot();
    }

    public void increaseNonce() {
        this.nonce++;
    }
}