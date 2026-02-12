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

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

public class DistributedServerTest {

    @Test
    public void testNetworkPropagationAndConsensus() throws Exception {

        SimpleAccountFactory factory =
                new SimpleAccountFactory(new SimpleKeyFactory());

        Account accountA = factory.buildAccount();
        Account accountB = factory.buildAccount();

        DistributedTimestampServer nodeA =
                new DistributedTimestampServer(accountA, 1000,
                        new SimpleBlockMiner(2), 9090);

        nodeA.addPeer("127.0.0.1:9089");

        DistributedTimestampServer nodeB =
                new DistributedTimestampServer(
                        new ArrayList<>(nodeA.getBlocks()),
                        new HashSet<>(nodeA.getUtxoSet()),
                        new SimpleBlockMiner(2),
                        9089);

        nodeB.addPeers("127.0.0.1:9088", "127.0.0.1:9090");

        DistributedTimestampServer nodeC =
                new DistributedTimestampServer(
                        new ArrayList<>(nodeA.getBlocks()),
                        new HashSet<>(nodeA.getUtxoSet()),
                        new SimpleBlockMiner(2),
                        9088);

        nodeC.addPeer("127.0.0.1:9089");

        // Wait until all nodes are listening
        waitUntilPortOpen(9090, 5000);
        waitUntilPortOpen(9089, 5000);
        waitUntilPortOpen(9088, 5000);

        Transaction tx = new SimpleTransactionClient().createTransaction(
                accountA.getPublicKey(),
                accountA.getPrivateKey(),
                10,
                accountB.getPublicKey(),
                nodeA.getTransactionInputs(accountA.getPublicKey())
        );

        nodeA.onReceiveTransaction(tx);

        // ✅ Wait for propagation to C
        boolean propagated = waitUntil(
                () -> nodeC.getTransactionPool().contains(tx),
                30000
        );

        Assertions.assertTrue(propagated,
                "Transaction should propagate from Node A to Node C");

        Assertions.assertEquals(1, nodeA.getTransactionPool().size());
        Assertions.assertEquals(1, nodeB.getTransactionPool().size());
        Assertions.assertEquals(1, nodeC.getTransactionPool().size());

        // Start mining on B
        boolean mined = nodeB.mineCurrentBlock(10000);
        Assertions.assertTrue(mined);

        nodeB.broadcastLastBlock();

        // ✅ Wait for consensus
        boolean consensusReached = waitUntil(
                () -> nodeA.getCurrentBlockIdx() == nodeB.getCurrentBlockIdx()
                        && nodeC.getCurrentBlockIdx() == nodeB.getCurrentBlockIdx(),
                30000
        );

        Assertions.assertTrue(consensusReached,
                "All nodes should sync to same height");

        Assertions.assertArrayEquals(nodeA.getHash(), nodeB.getHash());
        Assertions.assertArrayEquals(nodeB.getHash(), nodeC.getHash());

        Assertions.assertEquals(0, nodeA.getTransactionPool().size());
        Assertions.assertEquals(0, nodeB.getTransactionPool().size());
        Assertions.assertEquals(0, nodeC.getTransactionPool().size());
    }

    // ================= Helpers =================

    private boolean waitUntil(Supplier<Boolean> condition, long timeoutMs)
            throws InterruptedException {

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMs) {
            if (condition.get()) {
                return true;
            }
            Thread.sleep(100);
        }
        return false;
    }

    private void waitUntilPortOpen(int port, long timeoutMs)
            throws Exception {

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMs) {
            try (Socket ignored = new Socket("127.0.0.1", port)) {
                return;
            } catch (IOException ignored) {
                Thread.sleep(100);
            }
        }

        throw new RuntimeException("Port " + port + " did not open in time");
    }
}
