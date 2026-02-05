package org.sepehr.jblockchain.timestampserver;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.Getter;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Block {

    private final List<Transaction> items = new ArrayList<>();
    private final byte[] prevHash;
    private int nonce;

    public Block(KeyPair starterKeyPair) {
        Utxo input = new Utxo(starterKeyPair.getPublic(), 21_000_000, "".getBytes(), 0);
        input.setConfirmed(true);
        this.prevHash = Hashing.sha256().hashBytes("".getBytes()).asBytes();
        SimpleTransactionManager simpleTransactionManager = new SimpleTransactionManager();
        Transaction transaction = simpleTransactionManager.createTransaction(
                starterKeyPair.getPublic(),
                starterKeyPair.getPrivate(),
                21_000_000,
                starterKeyPair.getPublic(),
                List.of(input)
        );
        transaction.getOut1().setConfirmed(true);
        transaction.getOut0().setConfirmed(true);
        Hasher hasher = Hashing.sha256().newHasher();
        hasher.putBytes(input.getTxid());
        hasher.putBytes(starterKeyPair.getPublic().getEncoded());
        byte[] transactionHash = hasher.hash().asBytes();
        transaction.setHash(transactionHash);
        items.add(transaction);
    }

    public Block(byte[] prevHash) {
        this.prevHash = prevHash;
    }

    public byte[] getHash() {
        Hasher hasher = Hashing.sha256().newHasher();
        items.forEach(transaction -> hasher.putBytes(transaction.getHash()));
        hasher.putBytes(ByteBuffer.allocateDirect(nonce));
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