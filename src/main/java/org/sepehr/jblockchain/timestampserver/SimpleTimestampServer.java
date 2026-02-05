package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.util.ArrayList;
import java.util.List;

public class SimpleTimestampServer implements TimestampServer {

    public SimpleTimestampServer() {

    }

    @Override
    public boolean appendTransaction(Transaction transaction) {
        blocks.get(blocks.size()-1).appendTransaction(transaction);
        return true;
    }

    @Override
    public void acceptBlock(Block block) {
        this.blocks.add(block);
    }

    @Override
    public byte[] getHash() {
        return blocks.get(blocks.size()-1).getHash();
    }

    private final List<Block> blocks = new ArrayList<>();

}
