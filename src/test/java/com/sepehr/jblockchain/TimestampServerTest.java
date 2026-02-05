package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.imp.AccountFactoryImp;
import org.sepehr.jblockchain.factory.imp.KeyFactoryImp;
import org.sepehr.jblockchain.sample.Account;
import org.sepehr.jblockchain.timestampserver.SimpleTimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionManager;
import org.sepehr.jblockchain.transaction.Transaction;

public class TimestampServerTest {

    AccountFactoryImp accountFactory = new AccountFactoryImp(new KeyFactoryImp());
    SimpleTransactionManager transactionFactory = new SimpleTransactionManager();

    @Test
    void preventDoubleSpendingTest() {
        Account sender1 = accountFactory.buildAccount();
        Account sender2 = accountFactory.buildAccount();

        Transaction transaction1 = transactionFactory.createTransaction(sender1.getPublicKey(), sender1.getPrivateKey(), "".getBytes());
        SimpleTimestampServer timestampServer = new SimpleTimestampServer(transaction1);

        Transaction transaction2 = transactionFactory.createTransaction(sender2.getPublicKey(), sender2.getPrivateKey(), transaction1.getHash());
        Assertions.assertTrue(timestampServer.appendTransaction(transaction2));

        Transaction transaction3 = transactionFactory.createTransaction(sender2.getPublicKey(), sender2.getPrivateKey(), transaction1.getHash());
        Assertions.assertFalse(timestampServer.appendTransaction(transaction3));
    }

}
