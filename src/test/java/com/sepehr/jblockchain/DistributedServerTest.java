package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jitcoin.account.Account;
import org.sepehr.jitcoin.account.SimpleAccountFactory;
import org.sepehr.jitcoin.account.SimpleKeyFactory;
import org.sepehr.jitcoin.network.DistributedTimestampServer;
import org.sepehr.jitcoin.proofwork.SimpleBlockMiner;
import org.sepehr.jitcoin.transaction.SimpleTransactionClient;
import org.sepehr.jitcoin.transaction.Transaction;

import java.util.concurrent.ExecutionException;

public class DistributedServerTest {

    @Test
    public void testNetworkPropagationAndConsensus() throws InterruptedException, ExecutionException {
        SimpleAccountFactory factory = new SimpleAccountFactory(new SimpleKeyFactory());
        Account accountA = factory.buildAccount();
        Account accountB = factory.buildAccount();

        DistributedTimestampServer nodeA = new DistributedTimestampServer(accountA, 1000, new SimpleBlockMiner(2), 9091);
        nodeA.addPeer("127.0.0.1:9092");
        DistributedTimestampServer nodeB = new DistributedTimestampServer(accountA, 1000, new SimpleBlockMiner(2), 9092);
        nodeB.addPeers("localhost:9091", "127.0.0.1:9093");
        DistributedTimestampServer nodeC = new DistributedTimestampServer(accountA, 1000, new SimpleBlockMiner(2),  9093);
        nodeC.addPeer("127.0.0.1:9092");

        Thread.sleep(500);

        Transaction tx = new SimpleTransactionClient().createTransaction(
                accountA.getPublicKey(), accountA.getPrivateKey(), 10,
                accountB.getPublicKey(), nodeA.getTransactionInputs(accountA.getPublicKey())
        );

        System.out.println("Step 1: Appending transaction to Node A...");
        nodeA.onReceiveTransaction(tx);

        Thread.sleep(6000);

        Assertions.assertTrue(nodeC.getCurrentBlock().getItems().contains(tx),
                "Transaction should propagate from Node A to Node C through Node B");
        System.out.println("Step 2: Transaction successfully reached Node C.");

        Assertions.assertEquals(1, nodeC.getTransactionPool().size());
        Assertions.assertEquals(1, nodeB.getTransactionPool().size());
        Assertions.assertEquals(1, nodeA.getTransactionPool().size());


        System.out.println("Step 3: Node B starts mining...");
        boolean mined = nodeB.mineCurrentBlock(5000);
        Assertions.assertTrue(mined);
        nodeB.broadcastLastBlock();

        Assertions.assertTrue(mined, "Node B should successfully mine the block");

        Thread.sleep(6000);

        Assertions.assertEquals(0, nodeB.getTransactionPool().size());
        Assertions.assertEquals(0, nodeA.getTransactionPool().size());
        Assertions.assertEquals(0, nodeC.getTransactionPool().size());


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
