package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.SimpleAccountFactory;
import org.sepehr.jblockchain.factory.SimpleKeyFactory;
import org.sepehr.jblockchain.factory.Account;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.util.List;

public class TransactionTest {

    SimpleAccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
    SimpleTransactionManager transactionFactory = new SimpleTransactionManager();

    @Test
    void transactionCreationVerifyTest() {
        Account sender = accountFactory.buildAccount();
        Account receiver = accountFactory.buildAccount();
        Assertions.assertNotEquals(sender.getPublicKey(), receiver.getPublicKey());
        Utxo utxo1 = new Utxo(sender.getPublicKey(), 300, "".getBytes(), 0);
        Utxo utxo2 = new Utxo(sender.getPublicKey(), 400, "".getBytes(), 0);
        Transaction transaction = transactionFactory.createTransaction(
                sender.getPublicKey(),
                sender.getPrivateKey(),
                500,
                receiver.getPublicKey(),
                List.of(utxo1, utxo2)
        );
        Assertions.assertTrue(transactionFactory.verifyTransaction(transaction));
    }

    @Test
    void doubleSpendingTest() {
        Account sender = accountFactory.buildAccount();
        Account receiver = accountFactory.buildAccount();
        Account receiver2 = accountFactory.buildAccount();
        Account receiver3 = accountFactory.buildAccount();

        List<Utxo> utxos = List.of(
                new Utxo(sender.getPublicKey(), 500, "".getBytes(), 0),
                new Utxo(sender.getPublicKey(), 600, "".getBytes(), 1)
        );

        Transaction transaction1 = transactionFactory.createTransaction(
                sender.getPublicKey(),
                sender.getPrivateKey(),
                600,
                receiver.getPublicKey(),
                utxos
        );


        Transaction transaction2 = transactionFactory.createTransaction(
                receiver.getPublicKey(),
                receiver.getPrivateKey(),
                300,
                receiver2.getPublicKey(),
                List.of(transaction1.getOut0())
        );

        Assertions.assertEquals(transaction2.getOut0().getReceiver(), receiver2.getPublicKey());


        Transaction transaction3 = transactionFactory.createTransaction(
                receiver.getPublicKey(),
                receiver.getPrivateKey(),
                100,
                receiver3.getPublicKey(),
                List.of(transaction1.getOut0())
        );

        Assertions.assertTrue(transactionFactory.verifyTransaction(transaction1));
        Assertions.assertTrue(transactionFactory.verifyTransaction(transaction2));
        Assertions.assertTrue(transactionFactory.verifyTransaction(transaction3));
    }

}
