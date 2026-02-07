package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.account.SimpleAccountFactory;
import org.sepehr.jblockchain.account.SimpleKeyFactory;
import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;
import org.sepehr.jblockchain.timestampserver.SimpleTimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionClient;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;
import org.sepehr.jblockchain.verification.MerkleTree;

import java.util.List;

public class TimestampServerTest {

    SimpleAccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
    SimpleTransactionClient transactionClient = new SimpleTransactionClient();

    @Test
    void timestampServerTest() {
        Account baseAccount = accountFactory.baseAccount();
        Account receiver1 = accountFactory.buildAccount();
        Account receiver2 = accountFactory.buildAccount();
        Account receiver3 = accountFactory.buildAccount();

        final SimpleTimestampServer timestampServer = new SimpleTimestampServer(
                baseAccount,
                21_000_000
        );
        Assertions.assertEquals(0, timestampServer.getCurrentBlockIdx());

        List<Utxo> inputs1 = timestampServer.getTransactionInputs(baseAccount.getPublicKey());
        Transaction transaction1 = transactionClient.createTransaction(
                baseAccount.getPublicKey(),
                baseAccount.getPrivateKey(),
                500,
                receiver1.getPublicKey(),
                inputs1
        );
        Assertions.assertNotNull(transaction1);
        timestampServer.appendTransaction(transaction1);
        Assertions.assertTrue(timestampServer.mineCurrentBlock(Long.MAX_VALUE));
        Assertions.assertEquals(1, timestampServer.getCurrentBlockIdx());

        List<Utxo> inputs2 = timestampServer.getTransactionInputs(receiver1.getPublicKey());
        Transaction transaction2 = transactionClient.createTransaction(
                receiver1.getPublicKey(),
                receiver1.getPrivateKey(),
                400,
                receiver2.getPublicKey(),
                inputs2
        );

        Assertions.assertNotNull(transaction2);
        Assertions.assertTrue(timestampServer.appendTransaction(transaction2));
        // Prevent double spending in current block
        Assertions.assertFalse(timestampServer.appendTransaction(transaction2));
        Assertions.assertTrue(timestampServer.mineCurrentBlock(Long.MAX_VALUE));
        Assertions.assertEquals(2, timestampServer.getCurrentBlockIdx());

        // Prevent double spending
        Assertions.assertFalse(timestampServer.appendTransaction(transaction2));

        List<Utxo> inputs3 = timestampServer.getTransactionInputs(receiver1.getPublicKey());
        Assertions.assertEquals(100, inputs3.stream().map(Utxo::getValue).reduce(Long::sum).get());
        Transaction transaction3 = transactionClient.createTransaction(
                receiver1.getPublicKey(),
                receiver1.getPrivateKey(),
                400,
                receiver3.getPublicKey(),
                inputs3
        );
        Assertions.assertNotNull(transaction3);
        Assertions.assertFalse(timestampServer.appendTransaction(transaction3));

        MerkleTree.TransactionProof proof = timestampServer.getProof(transaction2);
        Assertions.assertTrue(transactionClient.verifyTransaction(transaction2, proof));
    }

}
