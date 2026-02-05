package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.SimpleAccountFactory;
import org.sepehr.jblockchain.factory.SimpleKeyFactory;
import org.sepehr.jblockchain.factory.Account;
import org.sepehr.jblockchain.timestampserver.SimpleTimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.security.KeyPair;
import java.util.List;

public class TimestampServerTest {

    SimpleAccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
    SimpleTransactionManager transactionFactory = new SimpleTransactionManager();

    @Test
    void preventDoubleSpendingTest() {
        SimpleKeyFactory simpleKeyFactory = new SimpleKeyFactory();
        KeyPair keyPair = simpleKeyFactory.creatorKeyPair();

        Account baseAccount = new Account(keyPair.getPrivate(), keyPair.getPublic());
        Account receiver1 = accountFactory.buildAccount();
        Account receiver2 = accountFactory.buildAccount();
        Account receiver3 = accountFactory.buildAccount();
        Account receiver4 = accountFactory.buildAccount();
        final SimpleTimestampServer timestampServer = new SimpleTimestampServer(keyPair, new SimpleTransactionManager());

        Transaction transaction = timestampServer.createTransaction(
                baseAccount.getPublicKey(),
                baseAccount.getPrivateKey(),
                500,
                receiver1.getPublicKey()
        );
        Assertions.assertNotNull(transaction);

//        Transaction transaction1 = transactionFactory.createTransaction(baseAccount.getPublicKey(), baseAccount.getPrivateKey(), 400,
//                receiver1.getPublicKey(), List.of(new Utxo(sender.getPublicKey(), 1000, "".getBytes(), 0)));
//        SimpleKeyFactory keyFactory = new SimpleKeyFactory();
//        SimpleTimestampServer timestampServer = new SimpleTimestampServer(keyFactory.creatorKeyPair(), new SimpleTransactionManager());
//        Assertions.assertTrue(timestampServer.appendTransaction(transaction1));
//
//        Transaction transaction2 = transactionFactory.createTransaction(receiver.getPublicKey(), receiver.getPrivateKey(), 300,
//                receiver2.getPublicKey(), List.of(transaction1.getOut0()));
//        Assertions.assertTrue(timestampServer.appendTransaction(transaction2));
//
//        Transaction transaction3 = transactionFactory.createTransaction(receiver2.getPublicKey(), receiver2.getPrivateKey(), 100,
//                receiver3.getPublicKey(), List.of(transaction1.getOut0())); // Spend transaction
//        Assertions.assertFalse(timestampServer.appendTransaction(transaction3));
    }

}
