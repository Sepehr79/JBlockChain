package org.sepehr.jitcoin.timestampserver;

import lombok.Getter;
import org.sepehr.jitcoin.account.Account;
import org.sepehr.jitcoin.proofwork.BlockMiner;
import org.sepehr.jitcoin.proofwork.SimpleBlockMiner;
import org.sepehr.jitcoin.transaction.Transaction;
import org.sepehr.jitcoin.transaction.Utxo;
import org.sepehr.jitcoin.verification.MerkleTree;

import java.security.PublicKey;
import java.security.Signature;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleTimestampServer implements TimestampServer {

    private Block currentBlock;

    private List<Block> blocks = new ArrayList<>();

    // Prevent double spending in all blocks
    @Getter
    private Set<Utxo> utxoSet = new HashSet<>();
    // Prevent double spending in current block
    private final Set<Utxo> currentBlockUtxoSet = new HashSet<>();

    @Getter
    private final BlockMiner blockMiner;


    public SimpleTimestampServer(Account baseAccount,
                                 long maxSupply,
                                 BlockMiner blockMiner) {
        currentBlock = new Block(baseAccount, maxSupply);
        this.blockMiner = blockMiner;
        if (!blockMiner.mine(this.currentBlock, Long.MAX_VALUE)) {
            throw new RuntimeException("Genesis block mining failed");
        }
        this.acceptBlock(currentBlock);
    }

    public SimpleTimestampServer(List<Block> blocks, Set<Utxo> utxoSet, BlockMiner blockMiner) {
        this.blocks = blocks;
        this.utxoSet = utxoSet;
        this.blockMiner = blockMiner;
        Block lastBlock = blocks.get(blocks.size() - 1);
        this.currentBlock = new Block(lastBlock.getHash(), lastBlock.getIdx() + 1);
    }

    @Override
    public boolean acceptBlock(Block block) {
        if (blockMiner.verifyBlock(block) &&
                verifyTransactions(block.getItems())) {

            for (Transaction transaction : block.getItems()) {
                for (Utxo input : transaction.getInputs()) {
                    if (!utxoSet.remove(input) && blocks.size() != 0) {
                        return false;
                    }
                }

                updateUtxo(transaction.getOut0());
                updateUtxo(transaction.getOut1());

            }

            this.blocks.add(block);
            this.currentBlock = new Block(block.getHash(), block.getIdx() + 1);
            this.currentBlockUtxoSet.clear();
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
        if (verifyTransactions(List.of(transaction))) {
            for (Utxo input: transaction.getInputs()) {
                if (currentBlockUtxoSet.contains(input))
                    return false;
            }
            currentBlockUtxoSet.addAll(transaction.getInputs());
            this.currentBlock.appendTransaction(transaction);
            return true;
        }
        return false;
    }

    private boolean verifyTransactions(List<Transaction> transactions) {
        for (Transaction transaction: transactions) {
            if (!verifyTransaction(transaction) && blocks.size() != 0)
                return false;
        }
        return true;
    }

    @Override
    public List<Utxo> getTransactionInputs(PublicKey senderPublic) {
        return utxoSet.stream()
                .filter(u -> u.getReceiver().equals(senderPublic))
                .collect(Collectors.toList());
    }

    @Override
    public int getCurrentBlockIdx() {
        return blocks.get(blocks.size()-1).getIdx();
    }

    @Override
    public MerkleTree.TransactionProof getProof(Transaction transaction) {
        for (Block block: blocks) {
            if (block.getItems().contains(transaction))
                return new MerkleTree.TransactionProof(block.getMerkleTree().getProof(transaction), block.getTransactionRootHash());
        }
        return null;
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    public Block getCurrentBlock() {
        return this.currentBlock;
    }

    private boolean verifyTransaction(Transaction transaction) {
        try {
            if (transaction.getOut1().getValue() < 0 ) return false;
            List<Utxo> inputs = transaction.getInputs();
            for (Utxo utxo : inputs) {
                if (!utxoSet.contains(utxo)) return false;

                if (!Arrays.equals(utxo.getReceiver().getEncoded(), transaction.getSender().getEncoded())) {
                    return false;
                }

            }

            var signature = Signature.getInstance("SHA256withDSA");
            signature.initVerify(transaction.getSender());
            signature.update(transaction.getHash());
            return signature.verify(transaction.getTransactionSignature());

        } catch (Exception e) {
            return false;
        }
    }

    private void updateUtxo(Utxo utxo) {
        if (utxo != null && utxo.getValue() > 0) {
            utxo.setConfirmed(true);
            utxo.setBlockHeight(currentBlock.getIdx());
            utxoSet.add(utxo);
        }
    }

}
