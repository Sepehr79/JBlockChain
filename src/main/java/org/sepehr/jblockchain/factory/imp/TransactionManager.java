package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.sample.Transaction;

import java.security.*;

public class TransactionManager {

    public Transaction buildTransaction(final PrivateKey privateKey, final Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        final Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
        signature.initSign(privateKey);
        signature.update(transaction.toString().getBytes());
        byte[] sign = signature.sign();
        transaction.setTransactionSignature(sign);
        return transaction;
    }

    public boolean verifyTransaction(final PublicKey publicKey, final Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        final Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
        signature.initVerify(publicKey);
        signature.update(transaction.toString().getBytes());
        return signature.verify(transaction.getTransactionSignature());
    }

}
