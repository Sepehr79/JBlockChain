package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.account.SimpleAccountFactory;
import org.sepehr.jblockchain.account.SimpleKeyFactory;
import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;
import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.timestampserver.SimpleTimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.util.List;

public class TimestampServerTest {

    SimpleAccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
    SimpleTransactionManager transactionManager = new SimpleTransactionManager();

    @Test
    void preventDoubleSpendingTest() {
        Account baseAccount = accountFactory.baseAccount();
        Account receiver1 = accountFactory.buildAccount();
        Account receiver2 = accountFactory.buildAccount();
        Account receiver3 = accountFactory.buildAccount();

        final SimpleTimestampServer timestampServer = new SimpleTimestampServer(
                baseAccount,
                new SimpleTransactionManager(),
                new SimpleBlockMiner(2)
        );

        List<Utxo> inputs1 = timestampServer.getInputs(baseAccount.getPublicKey());
        Transaction transaction1 = transactionManager.createTransaction(
                baseAccount.getPublicKey(),
                baseAccount.getPrivateKey(),
                500,
                receiver1.getPublicKey(),
                inputs1
        );
        Assertions.assertNotNull(transaction1);
        timestampServer.appendTransaction(transaction1);
        Block block = timestampServer.mineCurrentBlock(Long.MAX_VALUE);
        Assertions.assertTrue(timestampServer.acceptBlock(block));

        List<Utxo> inputs2 = timestampServer.getInputs(receiver1.getPublicKey());
        Transaction transaction2 = transactionManager.createTransaction(
                receiver1.getPublicKey(),
                receiver1.getPrivateKey(),
                400,
                receiver2.getPublicKey(),
                inputs2
        );

        Assertions.assertNotNull(transaction2);
        Assertions.assertTrue(timestampServer.appendTransaction(transaction2));
        Assertions.assertFalse(timestampServer.appendTransaction(transaction2));
        Block block2 = timestampServer.mineCurrentBlock(Long.MAX_VALUE);
        Assertions.assertTrue(timestampServer.acceptBlock(block2));

        // Prevent double spending
        Assertions.assertFalse(timestampServer.appendTransaction(transaction2));

        List<Utxo> inputs3 = timestampServer.getInputs(receiver1.getPublicKey());
        Assertions.assertEquals(100, inputs3.stream().map(Utxo::getValue).reduce(Long::sum).get());
        Transaction transaction3 = transactionManager.createTransaction(
                receiver1.getPublicKey(),
                receiver1.getPrivateKey(),
                400,
                receiver3.getPublicKey(),
                inputs3
        );
        Assertions.assertNotNull(transaction3);
        Assertions.assertFalse(timestampServer.appendTransaction(transaction3));
    }

}
