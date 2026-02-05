package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.transaction.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleTimestampServer implements TimestampServer {

    public SimpleTimestampServer(Transaction firstTransaction) {
        Block firstBlock = new Block("".getBytes());
        firstBlock.appendTransaction(firstTransaction);
        blocks.add(firstBlock);
    }

    @Override
    public boolean appendTransaction(Transaction transaction) {
        if (isDuplicateTransaction(transaction))
            return false;
        blocks.get(blocks.size()-1).appendTransaction(transaction);
        return true;
    }

    @Override
    public boolean isDuplicateTransaction(Transaction transaction) {
        for (Block block : blocks) {
            for (Transaction t : block.getItems()) {
                if (Arrays.equals(t.getPrevHash(), transaction.getPrevHash()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public byte[] getHash() {
        return blocks.get(blocks.size()-1).getHash();
    }

    private final List<Block> blocks = new ArrayList<>();

}
