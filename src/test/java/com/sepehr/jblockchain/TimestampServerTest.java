package com.sepehr.jblockchain;

import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.imp.AccountFactoryImp;
import org.sepehr.jblockchain.factory.imp.KeyFactoryImp;
import org.sepehr.jblockchain.sample.Account;
import org.sepehr.jblockchain.timestampserver.TimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionFactory;
import org.sepehr.jblockchain.transaction.Transaction;

public class TimestampServerTest {

    AccountFactoryImp accountFactory = new AccountFactoryImp(new KeyFactoryImp());
    SimpleTransactionFactory transactionFactory = new SimpleTransactionFactory();

    @Test
    void preventDoubleSpendingAndInvalidTransactionTest() {
        Account sender1 = accountFactory.buildAccount();
        Account sender2 = accountFactory.buildAccount();

        Transaction transaction1 = transactionFactory.createTransaction(sender1.getPublicKey(), sender1.getPrivateKey(), "".getBytes());
        TimestampServer timestampServer = new TimestampServer(transaction1);

        Transaction transaction2 = transactionFactory.createTransaction(sender2.getPublicKey(), sender2.getPrivateKey(), transaction1.getHash());
        Assertions.assertTrue(timestampServer.appendTransaction(transaction2));

        Transaction transaction3 = transactionFactory.createTransaction(sender2.getPublicKey(), sender2.getPrivateKey(), transaction1.getHash());
        Assertions.assertTrue(timestampServer.isDuplicateTransaction(transaction3));
        Assertions.assertTrue(timestampServer.isValidTransaction(transaction3));
        Assertions.assertFalse(timestampServer.appendTransaction(transaction3));

        Transaction transaction4 = transactionFactory.createTransaction(
                sender2.getPublicKey(),
                sender2.getPrivateKey(),
                Hashing.sha256().hashBytes("Fake hash".getBytes()).asBytes()
        );
        Assertions.assertFalse(timestampServer.isValidTransaction(transaction4));
    }

}
