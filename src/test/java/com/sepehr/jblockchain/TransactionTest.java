package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.imp.AccountFactoryImp;
import org.sepehr.jblockchain.factory.imp.KeyFactoryImp;
import org.sepehr.jblockchain.sample.Account;
import org.sepehr.jblockchain.transaction.SimpleTransactionFactory;
import org.sepehr.jblockchain.transaction.Transaction;

public class TransactionTest {

    AccountFactoryImp accountFactory = new AccountFactoryImp(new KeyFactoryImp());
    SimpleTransactionFactory transactionFactory = new SimpleTransactionFactory();

    @Test
    void transactionCreationVerifyTest() {
        Account sender = accountFactory.buildAccount();
        Transaction transaction = transactionFactory.createTransaction(
                sender.getPublicKey(),
                sender.getPrivateKey()
        );
        Assertions.assertTrue(transactionFactory.verifyTransaction(sender.getPublicKey(), transaction));
    }

    @Test
    void doubleSpendingTest() {
        Account sender1 = accountFactory.buildAccount();
        Account sender2 = accountFactory.buildAccount();

        Transaction transaction1 = transactionFactory.createTransaction(sender1.getPublicKey(), sender1.getPrivateKey());
        Transaction transaction2 = transactionFactory.createTransaction(sender2.getPublicKey(), sender2.getPrivateKey(), transaction1.getHash());
        Transaction transaction3 = transactionFactory.createTransaction(sender2.getPublicKey(), sender2.getPrivateKey(), transaction1.getHash());

        Assertions.assertTrue(transactionFactory.verifyTransaction(sender2.getPublicKey(), transaction2));
        Assertions.assertTrue(transactionFactory.verifyTransaction(sender2.getPublicKey(), transaction3));
    }

}
