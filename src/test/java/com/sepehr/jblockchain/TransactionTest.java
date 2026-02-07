package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.account.SimpleAccountFactory;
import org.sepehr.jblockchain.account.SimpleKeyFactory;
import org.sepehr.jblockchain.transaction.SimpleTransactionClient;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.security.*;
import java.util.List;

public class TransactionTest {

    SimpleAccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
    SimpleTransactionClient transactionFactory = new SimpleTransactionClient();

    @Test
    void transactionCreationVerifyTest() {
        Account sender = accountFactory.buildAccount();
        Account receiver = accountFactory.buildAccount();
        Assertions.assertNotEquals(sender.getPublicKey(), receiver.getPublicKey());
        Utxo utxo1 = new Utxo(sender.getPublicKey(), 300, "".getBytes(), 0);
        Utxo utxo2 = new Utxo(sender.getPublicKey(), 400, "".getBytes(), 0);
        utxo1.setConfirmed(true);
        utxo2.setConfirmed(true);
        List<Utxo> inputs = List.of(utxo1, utxo2);
        Transaction transaction = transactionFactory.createTransaction(
                sender.getPublicKey(),
                sender.getPrivateKey(),
                500,
                receiver.getPublicKey(),
                inputs
        );
        Assertions.assertTrue(verifyTransaction(transaction, inputs));
    }

    @Test
    void doubleSpendingTest() {
        Account sender = accountFactory.buildAccount();
        Account receiver = accountFactory.buildAccount();
        Account receiver2 = accountFactory.buildAccount();
        Account receiver3 = accountFactory.buildAccount();

        Utxo utxo1 = new Utxo(sender.getPublicKey(), 500, "".getBytes(), 0);
        Utxo utxo2 = new Utxo(sender.getPublicKey(), 600, "".getBytes(), 1);
        utxo1.setConfirmed(true);
        utxo2.setConfirmed(true);
        List<Utxo> inputs = List.of(utxo1, utxo2);

        Transaction transaction1 = transactionFactory.createTransaction(
                sender.getPublicKey(),
                sender.getPrivateKey(),
                600,
                receiver.getPublicKey(),
                inputs
        );

        transaction1.getOut0().setConfirmed(true);
        List<Utxo> inputs1 = List.of(transaction1.getOut0());
        Transaction transaction2 = transactionFactory.createTransaction(
                receiver.getPublicKey(),
                receiver.getPrivateKey(),
                500,
                receiver2.getPublicKey(),
                inputs
        );

        Assertions.assertEquals(transaction2.getOut0().getReceiver(), receiver2.getPublicKey());

        Transaction transaction3 = transactionFactory.createTransaction(
                receiver.getPublicKey(),
                receiver.getPrivateKey(),
                500,
                receiver3.getPublicKey(),
                inputs
        );

        Assertions.assertTrue(verifyTransaction(transaction1, inputs));
        Assertions.assertTrue(verifyTransaction(transaction2, inputs1));
        Assertions.assertTrue(verifyTransaction(transaction3, inputs1));
    }

    /**
     * Verify transaction signature and amount
     */
    boolean verifyTransaction(Transaction transaction, List<Utxo> inputs) {
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
}
