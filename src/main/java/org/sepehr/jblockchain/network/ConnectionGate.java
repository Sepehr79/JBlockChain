package org.sepehr.jblockchain.network;

import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.transaction.Transaction;

public interface ConnectionGate {

    void broadcastTransaction(Transaction transaction);

    void collectTransaction(Transaction transaction);

    void broadcastBlock(Block block);

    void acceptBlock(Block block);

    void lookupServers();

}
