package com.sepehr.jblockchain.factory;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.imp.AccountFactoryImp;
import org.sepehr.jblockchain.factory.imp.KeyFactoryImp;
import org.sepehr.jblockchain.factory.imp.RecoveryWordsFactoryImp;
import org.sepehr.jblockchain.factory.imp.TransactionManager;
import org.sepehr.jblockchain.sample.Account;
import org.sepehr.jblockchain.sample.Transaction;

import java.security.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionManagerTest {

    @Test
    void signatureAndDeSignatureTest() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        final AccountFactoryImp accountFactoryImp = new AccountFactoryImp(new KeyFactoryImp(), new RecoveryWordsFactoryImp());
        final TransactionManager transactionManager = new TransactionManager();

        Account account1 = accountFactoryImp.buildAccount();
        Account account2 = accountFactoryImp.buildAccount();

        final PublicKey sender = account1.getPublicKey();
        final PublicKey receiver = account2.getPublicKey();

        final Transaction transaction = new Transaction(
                sender,
                receiver,
                "1.5"
        );

        Transaction signedTransaction = transactionManager.buildTransaction(account1.getPrivateKey(), transaction);
        System.out.println(Arrays.toString(transaction.getTransactionSignature()));
        assertTrue(transactionManager.verifyTransaction(sender, signedTransaction));
    }

}
