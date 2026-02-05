package org.sepehr.jblockchain.timestampserver;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.Getter;
import org.sepehr.jblockchain.transaction.Transaction;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Block {

    private final List<Transaction> items = new ArrayList<>();
    private final byte[] prevHash;
    private int nonce;

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

    protected boolean isDuplicateTransaction(Transaction transaction) {
        for (Transaction t: items) {
            if (Arrays.equals(t.getPrevHash(), transaction.getPrevHash()))
                return true;
        }
        return false;
    }

    public void appendTransaction(Transaction transaction) {
        items.add(transaction);
    }

    public void increaseNonce() {
        this.nonce++;
    }
}