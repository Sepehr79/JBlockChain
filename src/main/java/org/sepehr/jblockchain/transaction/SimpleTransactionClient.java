package org.sepehr.jblockchain.transaction;

import org.sepehr.jblockchain.verification.HashManager;
import org.sepehr.jblockchain.verification.MerkleTree;

import java.security.*;
import java.util.List;

public class SimpleTransactionClient implements TransactionClient {

    @Override
    public Transaction createTransaction(PublicKey senderPublic,
                                         PrivateKey senderPrivate,
                                         long amount,
                                         PublicKey receiverPublic,
                                         List<Utxo> inputs) {
        try {
            var transaction = new Transaction(senderPublic, amount, receiverPublic);
            long sum = 0;
            for (Utxo utxo: inputs) {
                sum += utxo.getValue();
            }
            byte[] hash = HashManager.hashTransaction(transaction, inputs);
            transaction.setHash(hash);
            transaction.setOut0(new Utxo(receiverPublic, amount, hash, 0));
            transaction.setOut1(new Utxo(senderPublic, sum - amount, hash, 1));
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
    public boolean verifyTransaction(Transaction transaction, List<Utxo> inputs) {
        try {
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

    @Override
    public boolean verifyTransaction(Transaction transaction, MerkleTree.TransactionProof transactionProof) {
        return MerkleTree.verifyTransaction(transaction, transactionProof.getProofElement(), transactionProof.getRootHash());
    }


}
