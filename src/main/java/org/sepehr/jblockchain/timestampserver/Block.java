package org.sepehr.jblockchain.timestampserver;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.Getter;
import org.sepehr.jblockchain.transaction.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Block {

    private final List<Transaction> items = new ArrayList<>();
    private final byte[] prevHash;

    public Block(byte[] prevHash) {
        this.prevHash = prevHash;
    }

    public byte[] getHash() {
        Hasher hasher = Hashing.sha256().newHasher();
        items.forEach(transaction -> hasher.putBytes(transaction.getHash()));
        hasher.putBytes(prevHash);
        return hasher.hash().asBytes();
    }

    public boolean appendTransaction(Transaction transaction) {
        for (Transaction t: items) {
            if (Arrays.equals(t.getPrevHash(), transaction.getPrevHash()))
                return false;
        }
        items.add(transaction);
        return true;
    }
}
