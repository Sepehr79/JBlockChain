package org.sepehr.jblockchain.timestampserver;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.Getter;
import org.sepehr.jblockchain.transaction.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimestampServer {

    public TimestampServer(Transaction firstTransaction) {
        Block firstBlock = new Block("".getBytes());
        firstBlock.appendTransaction(firstTransaction);
        blocks.add(firstBlock);
    }

    public boolean appendTransaction(Transaction transaction) {
        Block last = blocks.get(blocks.size()-1);
        if(last.items.size() >= 20)
            blocks.add(new Block(last.prevHash));
        if (isDuplicateTransaction(transaction))
            return false;
        if (!isValidTransaction(transaction))
            return false;
        blocks.get(blocks.size()-1).appendTransaction(transaction);
        return true;
    }

    public boolean isDuplicateTransaction(Transaction transaction) {
        for (Block block: blocks)
            if (block.isDuplicateTransaction(transaction))
                return true;
        return false;
    }

    public boolean isValidTransaction(Transaction transaction) {
        for (Block block: blocks)
            if (block.isValidTransaction(transaction))
                return true;
        return false;
    }

    public byte[] getHash() {
        return blocks.get(blocks.size()-1).getHash();
    }

    @Getter
    public static class Block {

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

        protected boolean isDuplicateTransaction(Transaction transaction) {
            for (Transaction t: items) {
                if (Arrays.equals(t.getPrevHash(), transaction.getPrevHash()))
                    return true;
            }
            return false;
        }

        protected boolean isValidTransaction(Transaction transaction) {
            for (Transaction t: items)
                if (Arrays.equals(transaction.getPrevHash(), t.getHash()))
                    return true;
            return false;
        }

        private void appendTransaction(Transaction transaction) {
            items.add(transaction);
        }
    }

    private final List<Block> blocks = new ArrayList<>();



}
