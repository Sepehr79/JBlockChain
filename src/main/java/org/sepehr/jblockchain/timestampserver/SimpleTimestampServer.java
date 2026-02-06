package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.proofwork.BlockMiner;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.TransactionManager;
import org.sepehr.jblockchain.transaction.Utxo;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public boolean acceptBlock(Block block) {
        if (blockMiner.verifyBlock(block) && verifyTransactions(block.getItems()) && !sameSenderInBlock(block)) {
            int idx = block.getIdx();
            for (Transaction transaction: block.getItems()) {
                List<Utxo> inputs = getInputs(transaction.getSender());
                for (Utxo utxo: inputs) {
                    utxo.setSpent(true);
                }
                transaction.getOut0().setConfirmed(true);
                transaction.getOut0().setBlockHeight(idx);
                transaction.getOut1().setConfirmed(true);
                transaction.getOut1().setBlockHeight(idx);
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
    public boolean appendTransaction(Transaction transaction) {
        if (verifyTransactions(List.of(transaction)) && !sameSenderInBlock(currentBlock, transaction)) {
            this.currentBlock.appendTransaction(transaction);
            return true;
        }
        return false;
    }

    private boolean verifyTransactions(List<Transaction> transactions) {
        for (Transaction transaction: transactions) {
            List<Utxo> inputs = getInputs(transaction.getSender());
            if (!transactionManager.verifyTransaction(transaction, inputs))
                return false;
        }
        return true;
    }

    @Override
    public List<Utxo> getInputs(PublicKey senderPublic) {
        List<Utxo> inputs = new ArrayList<>();
        for (Block block: blocks)
            for (Transaction t: block.getItems()) {
                if (t.getOut0().getReceiver().equals(senderPublic) && !t.getOut0().isSpent() && t.getOut0().isConfirmed())
                    inputs.add(t.getOut0());
                if (t.getOut1().getReceiver().equals(senderPublic) && !t.getOut1().isSpent() && t.getOut1().isConfirmed())
                    inputs.add(t.getOut1());
            }
        return inputs;
    }

    private boolean sameSenderInBlock(Block block) {
        Set<PublicKey> publicKeys = new HashSet<>();
        block.getItems().forEach(transaction -> publicKeys.add(transaction.getSender()));
        return publicKeys.size() < block.getItems().size();
    }

    private boolean sameSenderInBlock(Block block, Transaction newTransaction) {
        Set<PublicKey> publicKeys = new HashSet<>();
        block.getItems().forEach(transaction -> publicKeys.add(transaction.getSender()));
        publicKeys.add(newTransaction.getSender());
        return publicKeys.size() < block.getItems().size() + 1;
    }


}
