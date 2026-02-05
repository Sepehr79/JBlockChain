package org.sepehr.jblockchain.transaction;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.security.*;
import java.util.List;

public class SimpleTransactionManager implements TransactionManager {

    @Override
    public Transaction createTransaction(PublicKey senderPublic,
                                         PrivateKey senderPrivate,
                                         long amount,
                                         PublicKey receiverPublic,
                                         List<Utxo> inputs) {
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
            transaction.setInputs(inputs);
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
    public boolean verifyTransaction(Transaction transaction) {
        try {
            var signature = Signature.getInstance("SHA1withDSA", "SUN");
            PublicKey receiver = transaction.getSender();
            long sum = 0;
            for (Utxo utxo: transaction.getInputs()) {
                if (!receiver.equals(utxo.getReceiver()) || utxo.isConfirmed() || utxo.isSpent())
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
