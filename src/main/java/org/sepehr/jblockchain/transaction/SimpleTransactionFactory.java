package org.sepehr.jblockchain.transaction;

import java.security.*;

public class SimpleTransactionFactory implements TransactionFactory{

    public Transaction createTransaction(PublicKey senderPublic, PrivateKey senderPrivate) {
        return createTransaction(senderPublic, senderPrivate, "".getBytes());
    }

    @Override
    public Transaction createTransaction(PublicKey senderPublic, PrivateKey senderPrivate, byte[] prevHash) {
        try {
            var transaction = new Transaction(senderPublic, prevHash);
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
    public boolean verifyTransaction(PublicKey senderPublic, Transaction transaction) {
        try {
            var signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initVerify(senderPublic);
            signature.update(transaction.getHash());
            return signature.verify(transaction.getTransactionSignature());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }


}
