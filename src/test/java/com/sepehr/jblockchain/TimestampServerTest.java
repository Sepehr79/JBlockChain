package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.SimpleAccountFactory;
import org.sepehr.jblockchain.factory.SimpleKeyFactory;
import org.sepehr.jblockchain.factory.Account;
import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;
import org.sepehr.jblockchain.timestampserver.Block;
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

        Account baseAccount = accountFactory.baseAccount();
        Account receiver1 = accountFactory.buildAccount();
        Account receiver2 = accountFactory.buildAccount();
        Account receiver3 = accountFactory.buildAccount();
        Account receiver4 = accountFactory.buildAccount();

        final SimpleTimestampServer timestampServer = new SimpleTimestampServer(
                baseAccount,
                new SimpleTransactionManager(),
                new SimpleBlockMiner(2)
        );

        Transaction transaction1 = timestampServer.createTransaction(
                baseAccount.getPublicKey(),
                baseAccount.getPrivateKey(),
                500,
                receiver1.getPublicKey()
        );
        Assertions.assertNotNull(transaction1);
        timestampServer.appendTransaction(transaction1);
        Block block = timestampServer.mineCurrentBlock(Long.MAX_VALUE);
        Assertions.assertTrue(timestampServer.acceptBlock(block));


        Transaction transaction2 = timestampServer.createTransaction(
                receiver1.getPublicKey(),
                receiver1.getPrivateKey(),
                400,
                receiver2.getPublicKey()
        );

        Assertions.assertNotNull(transaction2);
        Assertions.assertTrue(timestampServer.appendTransaction(transaction2));
        Block block2 = timestampServer.mineCurrentBlock(Long.MAX_VALUE);
        Assertions.assertTrue(timestampServer.acceptBlock(block2));

        // Prevent double spending
        Assertions.assertFalse(timestampServer.appendTransaction(transaction2));

        Transaction transaction3 = timestampServer.createTransaction(
                receiver1.getPublicKey(),
                receiver1.getPrivateKey(),
                400,
                receiver3.getPublicKey()
        );
        Assertions.assertNotNull(transaction3);
        Assertions.assertFalse(timestampServer.appendTransaction(transaction3));


//        Transaction transaction3 = transactionFactory.createTransaction(receiver2.getPublicKey(), receiver2.getPrivateKey(), 100,
//                receiver3.getPublicKey(), List.of(transaction1.getOut0())); // Spend transaction
//        Assertions.assertFalse(timestampServer.appendTransaction(transaction3));
    }

}
