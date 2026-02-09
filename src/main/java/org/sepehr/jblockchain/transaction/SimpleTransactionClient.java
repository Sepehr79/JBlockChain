package org.sepehr.jblockchain.transaction;

import org.sepehr.jblockchain.timestampserver.TimestampServer;
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
            long sum = inputs.stream().mapToLong(Utxo::getValue).sum();

            Utxo out0 = new Utxo(receiverPublic, amount, 0);
            Utxo out1 = new Utxo(senderPublic, sum - amount, 1);

            var transaction = new Transaction(senderPublic, inputs, out0, out1);

            byte[] txHash = HashManager.getInstance().hashTransaction(transaction);
            transaction.setHash(txHash);

            out0.setTxid(txHash);
            out1.setTxid(txHash);

            var signature = Signature.getInstance("SHA256withDSA");
            signature.initSign(senderPrivate);
            signature.update(txHash);
            transaction.setTransactionSignature(signature.sign());

            return transaction;
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyTransaction(Transaction transaction, MerkleTree.TransactionProof transactionProof) {
        return MerkleTree.verifyTransaction(transaction, transactionProof.getProofElement(), transactionProof.getRootHash());
    }

    @Override
    public List<Utxo> getAccountInputs(TimestampServer server, PublicKey owner) {
        return server.getTransactionInputs(owner);
    }


}
