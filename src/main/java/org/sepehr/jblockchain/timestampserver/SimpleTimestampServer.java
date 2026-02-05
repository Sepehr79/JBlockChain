package org.sepehr.jblockchain.timestampserver;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.sepehr.jblockchain.factory.SimpleKeyFactory;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.TransactionManager;
import org.sepehr.jblockchain.transaction.Utxo;

import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleTimestampServer implements TimestampServer {

    private final TransactionManager transactionManager;

    public SimpleTimestampServer(KeyPair starterKeyPair, SimpleTransactionManager simpleTransactionManager) {
        Block block = new Block(starterKeyPair);
        blocks.add(block);
        this.transactionManager = simpleTransactionManager;
    }

    public boolean appendTransaction(Transaction transaction) {
        List<Utxo> inputs = new ArrayList<>();
        for (Block block: blocks)
            for (Transaction t: block.getItems()) {
                if (t.getOut0().getReceiver().equals(transaction.getSender()) && !t.getOut0().isSpent() && t.getOut0().isConfirmed())
                    inputs.add(t.getOut0());
                if (t.getOut1().getReceiver().equals(transaction.getSender()) && !t.getOut1().isSpent() && t.getOut1().isConfirmed())
                    inputs.add(t.getOut1());
            }
        boolean transactionVerify = this.transactionManager.verifyTransaction(transaction, inputs);
        if (transactionVerify) {
            blocks.get(blocks.size() - 1).appendTransaction(transaction);
            inputs.forEach(utxo -> utxo.setSpent(true));
            return true;
        }
        return false;
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

        return getTransaction(senderPublic, senderPrivate, amount, receiverPublic, inputs);
//        Transaction transaction = new Transaction(senderPublic, amount, receiverPublic);
//        boolean transactionVerify = this.transactionManager.verifyTransaction(transaction, inputs);
//        if (transactionVerify) {
//            inputs.forEach(utxo -> utxo.setSpent(true));
//            return transaction;
//        }
//        return null;
    }

    public static Transaction getTransaction(PublicKey senderPublic, PrivateKey senderPrivate, long amount, PublicKey receiverPublic, List<Utxo> inputs) {
        try {
            var transaction = new Transaction(senderPublic, amount, receiverPublic);
            Hasher hasher = Hashing.sha256().newHasher();
            long sum = 0;
            for (Utxo utxo: inputs) {
                hasher.putBytes(utxo.getTxid());
                sum += utxo.getValue();
            }
            hasher.putBytes(senderPublic.getEncoded());
            byte[] transactionHash = hasher.hash().asBytes();
            transaction.setHash(transactionHash);
            transaction.setOut0(new Utxo(receiverPublic, amount, transactionHash, 0));
            transaction.setOut1(new Utxo(senderPublic, sum - amount, transactionHash, 1));
            var signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initSign(senderPrivate);
            signature.update(transaction.getHash());
            byte[] sign = signature.sign();
            transaction.setTransactionSignature(sign);
            return transaction;
        } catch (NoSuchAlgorithmException | SignatureException | NoSuchProviderException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void acceptBlock(Block block) {
        for (Transaction transaction: block.getItems()) {
            transaction.getOut0().setConfirmed(true);
            transaction.getOut1().setConfirmed(true);
        }
        this.blocks.add(block);
    }

    @Override
    public byte[] getHash() {
        return blocks.get(blocks.size()-1).getHash();
    }

    private final List<Block> blocks = new ArrayList<>();

}
