package org.sepehr.jblockchain.network;

import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;
import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.timestampserver.TimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;

public abstract class SimpleNetwork implements Network {

    private final SimpleTransactionManager transactionManager;

    private final TimestampServer timestampServer;

    private final SimpleBlockMiner blockMiner;

    private Block currentBlock;

    public SimpleNetwork(SimpleTransactionManager transactionManager, TimestampServer timestampServer, SimpleBlockMiner blockMiner) {
        this.transactionManager = transactionManager;
        this.timestampServer = timestampServer;
        this.blockMiner = blockMiner;
//        currentBlock = new Block(timestampServer.getHash());
    }


    @Override
    public abstract void broadcastTransaction(Transaction transaction);

    @Override
    public abstract void broadcastBlock(Block block);

    @Override
    public void collectTransaction(Transaction transaction) {
        currentBlock.appendTransaction(transaction);
    }

    @Override
    public void mineCurrentBock() {
        while (!blockMiner.mine(currentBlock, Long.MAX_VALUE))
        timestampServer.acceptBlock(currentBlock);
        Block minedBlock = currentBlock;
        broadcastBlock(minedBlock);
       // currentBlock = new Block(timestampServer.getHash());
    }

    @Override
    public boolean acceptBlock(Block block) {
//        if (!blockMiner.verifyBlock(block))
//            return false;
//        for (Transaction transaction: block.getItems()) {
//            if (transactionManager.verifyTransaction(transaction))
//                return false;
//        }
//        timestampServer.acceptBlock(block);
//        currentBlock = new Block(timestampServer.getHash());
        return true;
    }
}
