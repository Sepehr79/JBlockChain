package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.proofwork.BlockMiner;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.security.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleTimestampServer implements TimestampServer {

    private final BlockMiner blockMiner;

    private Block currentBlock;

    private final List<Block> blocks = new ArrayList<>();


    public SimpleTimestampServer(Account baseAccount,
                                 BlockMiner blockMiner) {
        currentBlock = new Block(baseAccount, 0);
        this.blockMiner = blockMiner;
        this.mineCurrentBlock(Long.MAX_VALUE);
        this.blocks.add(currentBlock);
        currentBlock = new Block(currentBlock.getPrevHash(), 1);
    }

    @Override
    public boolean acceptBlock(Block block) {
        if (blockMiner.verifyBlock(block) && verifyTransactions(block.getItems()) && !sameSenderInCurrentBlock(block)) {
            int idx = block.getIdx();
            for (Transaction transaction: block.getItems()) {
                List<Utxo> inputs = getTransactionInputs(transaction.getSender());
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
    public boolean mineCurrentBlock(long timeout) {
        if (blockMiner.mine(this.currentBlock, timeout)) {
            return this.acceptBlock(currentBlock);
        }
        throw new RuntimeException("Mining block timeout");
    }

    @Override
    public boolean appendTransaction(Transaction transaction) {
        if (verifyTransactions(List.of(transaction)) && !sameSenderInCurrentBlock(transaction)) {
            this.currentBlock.appendTransaction(transaction);
            return true;
        }
        return false;
    }

    private boolean verifyTransactions(List<Transaction> transactions) {
        for (Transaction transaction: transactions) {
            if (!verifyTransaction(transaction))
                return false;
        }
        return true;
    }

    @Override
    public List<Utxo> getTransactionInputs(PublicKey senderPublic) {
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

    @Override
    public int getCurrentBlockIdx() {
        return blocks.get(blocks.size()-1).getIdx();
    }

    private boolean sameSenderInCurrentBlock(Block block) {
        Set<PublicKey> publicKeys = new HashSet<>();
        block.getItems().forEach(transaction -> publicKeys.add(transaction.getSender()));
        return publicKeys.size() < block.getItems().size();
    }

    private boolean sameSenderInCurrentBlock(Transaction newTransaction) {
        Set<PublicKey> publicKeys = new HashSet<>();
        currentBlock.getItems().forEach(transaction -> publicKeys.add(transaction.getSender()));
        publicKeys.add(newTransaction.getSender());
        return publicKeys.size() < currentBlock.getItems().size() + 1;
    }

    private boolean verifyTransaction(Transaction transaction) {
        try {
            List<Utxo> inputs = getTransactionInputs(transaction.getSender());
            var signature = Signature.getInstance("SHA1withDSA", "SUN");
            PublicKey receiver = transaction.getSender();
            long sum = 0;
            for (Utxo utxo: inputs) {
                if (!receiver.equals(utxo.getReceiver()) || !utxo.isConfirmed() || utxo.isSpent())
                    return false;
                sum += utxo.getValue();
            }

            if (transaction.getAmount() > sum)
                return false;

            signature.initVerify(transaction.getSender());
            signature.update(transaction.getHash());
            return signature.verify(transaction.getTransactionSignature());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


}
