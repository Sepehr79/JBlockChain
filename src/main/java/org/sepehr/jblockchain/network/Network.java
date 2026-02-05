package org.sepehr.jblockchain.network;

import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.transaction.Transaction;

public interface Network {

    void broadcastTransaction(Transaction transaction);

    void collectTransaction(Transaction transaction);

    Block mineCurrentBock();

    void broadcastCurrentBlock();

    boolean acceptBlock(Block block);

}
