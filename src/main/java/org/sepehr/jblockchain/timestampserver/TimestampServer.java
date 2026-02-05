package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.transaction.Transaction;

public interface TimestampServer {
    boolean appendTransaction(Transaction transaction);

    void acceptBlock(Block block);

    byte[] getHash();
}
