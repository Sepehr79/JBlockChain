package org.sepehr.jblockchain.timestampserver;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import org.sepehr.jblockchain.factory.Account;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Block {

    @Setter
    private int idx;
    private final List<Transaction> items = new ArrayList<>();
    private final byte[] prevHash;
    private long nonce;

    public Block(Account baseAccount, int idx) {
        Utxo input = new Utxo(baseAccount.getPublicKey(), 21_000_000, "".getBytes(), 0);
        input.setConfirmed(true);
        this.prevHash = Hashing.sha256().hashBytes("".getBytes()).asBytes();
        SimpleTransactionManager simpleTransactionManager = new SimpleTransactionManager();
        Transaction transaction = simpleTransactionManager.createTransaction(
                baseAccount.getPublicKey(),
                baseAccount.getPrivateKey(),
                21_000_000,
                baseAccount.getPublicKey(),
                List.of(input)
        );
        transaction.getOut1().setConfirmed(true);
        transaction.getOut0().setConfirmed(true);
        Hasher hasher = Hashing.sha256().newHasher();
        hasher.putBytes(input.getTxid());
        hasher.putBytes(baseAccount.getPublicKey().getEncoded());
        byte[] transactionHash = hasher.hash().asBytes();
        transaction.setHash(transactionHash);
        items.add(transaction);
    }

    public Block(byte[] prevHash, int idx) {
        this.prevHash = prevHash;
        this.idx = idx;
    }

    public byte[] getHash() {
        Hasher hasher = Hashing.sha256().newHasher();
        items.forEach(transaction -> hasher.putBytes(transaction.getHash()));
        hasher.putLong(nonce);
        hasher.putInt(idx);
        hasher.putBytes(prevHash);
        return hasher.hash().asBytes();
    }

    public void appendTransaction(Transaction transaction) {
        items.add(transaction);
    }

    public void increaseNonce() {
        this.nonce++;
    }
}