package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;
import org.sepehr.jblockchain.verification.MerkleTree;

import java.security.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleTimestampServer implements TimestampServer {

    private Block currentBlock;

    private final List<Block> blocks = new ArrayList<>();

    private final Set<Utxo> utxoSet = new HashSet<>();

    private final Set<Utxo> currentBlockUtxoSet = new HashSet<>();


    public SimpleTimestampServer(Account baseAccount,
                                 long maxSupply) {
        currentBlock = new Block(baseAccount, maxSupply);
        if (!SimpleBlockMiner.getInstance().mine(this.currentBlock, Long.MAX_VALUE)) {
            throw new RuntimeException("Genesis block mining failed");
        }
        utxoSet.addAll(currentBlock.getItems().get(0).getInputs());
        utxoSet.add(currentBlock.getItems().get(0).getOut0());
        utxoSet.add(currentBlock.getItems().get(0).getOut1());
        this.acceptBlock(currentBlock);
    }

    @Override
    public boolean acceptBlock(Block block) {
        if (SimpleBlockMiner.getInstance().verifyBlock(block) &&
                verifyTransactions(block.getItems())) {

            for (Transaction transaction : block.getItems()) {
                for (Utxo input : transaction.getInputs()) {
                    if (!utxoSet.remove(input)) {
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

    private void updateUtxo(Utxo utxo) {
        if (utxo != null && utxo.getValue() > 0) {
            utxo.setConfirmed(true);
            utxoSet.add(utxo);
        }
    }

    @Override
    public byte[] getHash() {
        return blocks.get(blocks.size()-1).getHash();
    }

    @Override
    public boolean mineCurrentBlock(long timeout) {
        if (SimpleBlockMiner.getInstance().mine(this.currentBlock, timeout)) {
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
                else
                    currentBlockUtxoSet.add(input);
            }
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
            List<Utxo> inputs = transaction.getInputs();
            long inputSum = 0;
            for (Utxo utxo : inputs) {
                if (!utxoSet.contains(utxo)) return false;

                if (!Arrays.equals(utxo.getReceiver().getEncoded(), transaction.getSender().getEncoded())) {
                    return false;
                }

                inputSum += utxo.getValue();
            }

            if (transaction.getAmount() > inputSum) return false;

            var signature = Signature.getInstance("SHA256withDSA");
            signature.initVerify(transaction.getSender());
            signature.update(transaction.getHash());
            return signature.verify(transaction.getTransactionSignature());

        } catch (Exception e) {
            return false;
        }
    }


}
