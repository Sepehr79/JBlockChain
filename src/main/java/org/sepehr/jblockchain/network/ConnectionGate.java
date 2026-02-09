package org.sepehr.jblockchain.network;

import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.transaction.Transaction;

public interface ConnectionGate {

    void onReceiveTransaction(Transaction transaction);

    void onReceiveBlock(Block block);

    void broadcastTransaction(Transaction transaction);

    void broadcastBlock(Block block);

}
