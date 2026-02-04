package org.sepehr.jblockchain.transaction;

import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;

import java.security.*;

public class SimpleTransactionFactory implements TransactionFactory{

    @Override
    public Transaction createTransaction(PublicKey senderPublic, PrivateKey senderPrivate) {
        return createTransaction(senderPublic, senderPrivate, "");
    }

    public Transaction createTransaction(PublicKey senderPublic, PrivateKey senderPrivate, String prevHash) {
        byte[] transactionHash = Hashing.sha256().hashBytes(Bytes.concat(
                prevHash.getBytes(), senderPublic.getEncoded())
        ).asBytes();
        var transaction = new Transaction(senderPublic);
        transaction.setHash(transactionHash);
        try {
            final Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initSign(senderPrivate);
            signature.update(transaction.getHash());
            byte[] sign = signature.sign();
            transaction.setTransactionSignature(sign);
            return transaction;
        } catch (NoSuchAlgorithmException | SignatureException | NoSuchProviderException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyTransaction(PublicKey senderPublic, Transaction transaction) {
        try {
            final Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initVerify(senderPublic);
            signature.update(transaction.getHash());
            return signature.verify(transaction.getTransactionSignature());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }


}
