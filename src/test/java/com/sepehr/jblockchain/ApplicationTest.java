package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.account.AccountFactory;
import org.sepehr.jblockchain.account.SimpleAccountFactory;
import org.sepehr.jblockchain.account.SimpleKeyFactory;
import org.sepehr.jblockchain.timestampserver.SimpleTimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionClient;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.verification.MerkleTree;

public class ApplicationTest {

    @Test
    void testApplication() {
        AccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
        Account baseAccount = accountFactory.buildAccount();
        Account account1    = accountFactory.buildAccount();

        long maxSupply = 21_000_000;
        var timestampServer = new SimpleTimestampServer(baseAccount, maxSupply);
        var client = new SimpleTransactionClient();

        var baseAccountInputs = client.getAccountInputs(timestampServer, baseAccount.getPublicKey());
        Transaction transaction1 = client.createTransaction(
                baseAccount.getPublicKey(),
                baseAccount.getPrivateKey(),
                100,
                account1.getPublicKey(),
                baseAccountInputs
        );
        Assertions.assertTrue(timestampServer.appendTransaction(transaction1));
        Assertions.assertFalse(timestampServer.appendTransaction(transaction1)); // Prevent double spending
        Assertions.assertTrue(timestampServer.mineCurrentBlock(Long.MAX_VALUE));

        var account1Inputs = client.getAccountInputs(timestampServer, account1.getPublicKey());
        Assertions.assertEquals(1, account1Inputs.size());
        Assertions.assertEquals(100, account1Inputs.get(0).getValue());
        Transaction transaction2 = client.createTransaction(
                account1.getPublicKey(),
                account1.getPrivateKey(),
                200,
                baseAccount.getPublicKey(),
                account1Inputs
        );
        Assertions.assertFalse(timestampServer.appendTransaction(transaction2));

        MerkleTree.TransactionProof proof = timestampServer.getProof(transaction1);
        Assertions.assertTrue(client.verifyTransaction(transaction1, proof));
    }

}
