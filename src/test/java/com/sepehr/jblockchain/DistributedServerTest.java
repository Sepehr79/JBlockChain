package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.account.SimpleAccountFactory;
import org.sepehr.jblockchain.account.SimpleKeyFactory;
import org.sepehr.jblockchain.network.DistributedTimestampServer;
import org.sepehr.jblockchain.transaction.SimpleTransactionClient;
import org.sepehr.jblockchain.transaction.Transaction;

import java.util.List;

public class DistributedServerTest {

    @Test
    public void testNetworkPropagationAndConsensus() throws InterruptedException {
        SimpleAccountFactory factory = new SimpleAccountFactory(new SimpleKeyFactory());
        Account accountA = factory.buildAccount();
        Account accountB = factory.buildAccount();
        Account accountC = factory.buildAccount();

        DistributedTimestampServer nodeA = new DistributedTimestampServer(accountA, 1000, 9091, List.of(9092));
        DistributedTimestampServer nodeB = new DistributedTimestampServer(accountA, 1000, 9092, List.of(9091, 9093));
        DistributedTimestampServer nodeC = new DistributedTimestampServer(accountA, 1000, 9093, List.of(9092));

        Thread.sleep(500);

        Transaction tx = new SimpleTransactionClient().createTransaction(
                accountA.getPublicKey(), accountA.getPrivateKey(), 10,
                accountC.getPublicKey(), nodeA.getTransactionInputs(accountA.getPublicKey())
        );

        System.out.println("Step 1: Appending transaction to Node A...");
        nodeA.appendTransaction(tx);

        Thread.sleep(3000);

        Assertions.assertTrue(nodeC.getCurrentBlock().getItems().contains(tx),
                "Transaction should propagate from Node A to Node C through Node B");
        System.out.println("Step 2: Transaction successfully reached Node C.");

        System.out.println("Step 3: Node B starts mining...");
        boolean mined = nodeB.mineCurrentBlock(5000);

        Assertions.assertTrue(mined, "Node B should successfully mine the block");

        Thread.sleep(3000);

        long heightA = nodeA.getCurrentBlockIdx();
        long heightB = nodeB.getCurrentBlockIdx();
        long heightC = nodeC.getCurrentBlockIdx();

        Thread.sleep(3000);

        Assertions.assertEquals(heightB, heightA, "Node A should sync with Node B's block");
        Assertions.assertEquals(heightB, heightC, "Node C should sync with Node B's block");

        Assertions.assertArrayEquals(nodeA.getHash(), nodeC.getHash(),
                "All nodes must have the exact same last block hash");

        System.out.println("Success: Network consensus reached!");
    }

}
