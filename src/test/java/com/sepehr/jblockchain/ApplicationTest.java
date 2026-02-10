package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jitcoin.account.Account;
import org.sepehr.jitcoin.account.AccountFactory;
import org.sepehr.jitcoin.account.SimpleAccountFactory;
import org.sepehr.jitcoin.account.SimpleKeyFactory;
import org.sepehr.jitcoin.proofwork.SimpleBlockMiner;
import org.sepehr.jitcoin.timestampserver.SimpleTimestampServer;
import org.sepehr.jitcoin.transaction.SimpleTransactionClient;
import org.sepehr.jitcoin.transaction.Transaction;
import org.sepehr.jitcoin.transaction.Utxo;
import org.sepehr.jitcoin.verification.MerkleTree;

public class ApplicationTest {

    @Test
    void testApplication() {
        AccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
        Account baseAccount = accountFactory.buildAccount();
        Account account1    = accountFactory.buildAccount();

        long maxSupply = 21_000_000;
        var timestampServer = new SimpleTimestampServer(baseAccount, maxSupply, new SimpleBlockMiner(2));
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

        Utxo fakeInput = new Utxo(baseAccount.getPublicKey(), 100, 0);
        fakeInput.setTxid(account1Inputs.get(0).getTxid());
        account1Inputs.add(fakeInput);
        Transaction transaction2 = client.createTransaction(
                account1.getPublicKey(),
                account1.getPrivateKey(),
                150,
                baseAccount.getPublicKey(),
                account1Inputs
        );
        Assertions.assertFalse(timestampServer.appendTransaction(transaction2));
        Assertions.assertTrue(timestampServer.mineCurrentBlock(Long.MAX_VALUE));

        MerkleTree.TransactionProof proof = timestampServer.getProof(transaction1);
        Assertions.assertTrue(client.verifyTransaction(transaction1, proof));
    }

}
