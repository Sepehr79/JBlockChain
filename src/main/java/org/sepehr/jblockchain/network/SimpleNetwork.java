package org.sepehr.jblockchain.network;

import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.timestampserver.TimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;

public class SimpleNetwork implements Network {

    private final SimpleTransactionManager transactionManager;

    private final TimestampServer timestampServer;

    public SimpleNetwork(SimpleTransactionManager transactionManager, TimestampServer timestampServer) {
        this.transactionManager = transactionManager;
        this.timestampServer = timestampServer;
    }


    @Override
    public void broadcastTransaction(Transaction transaction) {

    }

    @Override
    public void collectTransaction(Transaction transaction) {

    }

    @Override
    public Block mineCurrentBock() {
        return null;
    }

    @Override
    public void broadcastCurrentBlock() {

    }

    @Override
    public boolean acceptBlock(Block block) {
        // Todo verify all transactions are valid
        for (Transaction transaction: block.getItems()) {
            if (transactionManager.verifyTransaction(transaction) ||
                    timestampServer.isDuplicateTransaction(transaction))
                return false;
        }


        return true;
    }
}
