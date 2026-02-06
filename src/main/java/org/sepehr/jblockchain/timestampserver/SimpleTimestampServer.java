package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.factory.Account;
import org.sepehr.jblockchain.proofwork.BlockMiner;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.TransactionManager;
import org.sepehr.jblockchain.transaction.Utxo;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleTimestampServer implements TimestampServer {

    private final TransactionManager transactionManager;

    private final BlockMiner blockMiner;

    private Block currentBlock;

    private final List<Block> blocks = new ArrayList<>();


    public SimpleTimestampServer(Account baseAccount,
                                 SimpleTransactionManager simpleTransactionManager,
                                 BlockMiner blockMiner) {
        currentBlock = new Block(baseAccount, 0);
        currentBlock.setIdx(0);
        this.transactionManager = simpleTransactionManager;
        this.blockMiner = blockMiner;
        this.mineCurrentBlock(Long.MAX_VALUE);
        this.blocks.add(currentBlock);
        currentBlock = new Block(currentBlock.getPrevHash(), 1);
    }

    @Override
    public Transaction createTransaction(PublicKey senderPublic,
                                         PrivateKey senderPrivate,
                                         long amount,
                                         PublicKey receiverPublic) {
        List<Utxo> inputs = new ArrayList<>();
        for (Block block: blocks)
            for (Transaction t: block.getItems()) {
                if (t.getOut0().getReceiver().equals(senderPublic) && !t.getOut0().isSpent() && t.getOut0().isConfirmed())
                    inputs.add(t.getOut0());
                if (t.getOut1().getReceiver().equals(senderPublic) && !t.getOut1().isSpent() && t.getOut1().isConfirmed())
                    inputs.add(t.getOut1());
            }

        return transactionManager.createTransaction(senderPublic, senderPrivate, amount, receiverPublic, inputs);
    }

    @Override
    public boolean acceptBlock(Block block) {
        if (blockMiner.verifyBlock(block)) {
            int idx = block.getIdx();
            for (Transaction transaction: block.getItems()) {
                transactionManager.verifyTransaction(transaction, transaction.getInputs());
                transaction.getOut0().setConfirmed(true);
                transaction.getOut0().setBlockHeight(idx);
                transaction.getOut1().setConfirmed(true);
                transaction.getOut1().setBlockHeight(idx);
                List<Utxo> inputs = transaction.getInputs();
                for (Utxo utxo: inputs) {
                    Block b = blocks.get(utxo.getBlockHeight());
                    for (Transaction t: b.getItems()) {
                        if (Arrays.equals(t.getOut0().getTxid(), utxo.getTxid()))
                            t.getOut0().setSpent(true);
                        if (Arrays.equals(t.getOut1().getTxid(), utxo.getTxid()))
                            t.getOut1().setSpent(true);
                    }
                }
            }
            this.blocks.add(block);
            currentBlock = new Block(block.getHash(), block.getIdx() + 1);
            return true;
        }
        return false;
    }

    @Override
    public byte[] getHash() {
        return blocks.get(blocks.size()-1).getHash();
    }

    @Override
    public Block mineCurrentBlock(long timeout) {
        if (blockMiner.mine(this.currentBlock, timeout)) {
            return currentBlock;
        }
        throw new RuntimeException("Mining block timeout");
    }

    @Override
    public void appendTransaction(Transaction transaction) {
        this.currentBlock.appendTransaction(transaction);
    }


}
