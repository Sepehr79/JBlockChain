package org.sepehr.jitcoin.network;

import org.sepehr.jitcoin.timestampserver.Block;
import org.sepehr.jitcoin.transaction.Transaction;

public interface ConnectionGate {

    void onReceiveTransaction(Transaction transaction);

    void onReceiveBlock(Block block);

    void broadcastTransaction(Transaction transaction);

    void broadcastBlock(Block block);

}
