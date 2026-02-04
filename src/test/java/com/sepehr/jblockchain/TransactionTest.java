package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.imp.AccountFactoryImp;
import org.sepehr.jblockchain.factory.imp.KeyFactoryImp;
import org.sepehr.jblockchain.sample.Account;
import org.sepehr.jblockchain.transaction.SimpleTransactionFactory;
import org.sepehr.jblockchain.transaction.Transaction;

public class TransactionTest {

    @Test
    void transactionCreationVerifyTest() {
        AccountFactoryImp accountFactory = new AccountFactoryImp(new KeyFactoryImp());
        Account sender = accountFactory.buildAccount();

        SimpleTransactionFactory factory = new SimpleTransactionFactory();
        Transaction transaction = factory.createTransaction(
                sender.getPublicKey(),
                sender.getPrivateKey()
        );
        Assertions.assertTrue(factory.verifyTransaction(sender.getPublicKey(), transaction));
    }

}
