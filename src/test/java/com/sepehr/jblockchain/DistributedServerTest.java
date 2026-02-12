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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

public class DistributedServerTest {

    @Test
    public void testNetworkPropagationAndConsensus() throws Exception {

        int portA = randomPort();
        int portB = randomPort();
        int portC = randomPort();

        SimpleAccountFactory factory =
                new SimpleAccountFactory(new SimpleKeyFactory());

        Account accountA = factory.buildAccount();
        Account accountB = factory.buildAccount();

        DistributedTimestampServer nodeA =
                new DistributedTimestampServer(accountA, 1000,
                        new SimpleBlockMiner(2), portA);

        DistributedTimestampServer nodeB =
                new DistributedTimestampServer(
                        new ArrayList<>(nodeA.getBlocks()),
                        new HashSet<>(nodeA.getUtxoSet()),
                        new SimpleBlockMiner(2),
                        portB);

        DistributedTimestampServer nodeC =
                new DistributedTimestampServer(
                        new ArrayList<>(nodeA.getBlocks()),
                        new HashSet<>(nodeA.getUtxoSet()),
                        new SimpleBlockMiner(2),
                        portC);

        nodeA.addPeer("127.0.0.1:" + portB);
        nodeB.addPeers(
                "127.0.0.1:" + portA,
                "127.0.0.1:" + portC
        );
        nodeC.addPeer("127.0.0.1:" + portB);

        waitUntilPortOpen(portA, 5000);
        waitUntilPortOpen(portB, 5000);
        waitUntilPortOpen(portC, 5000);

        Transaction tx = new SimpleTransactionClient().createTransaction(
                accountA.getPublicKey(),
                accountA.getPrivateKey(),
                10,
                accountB.getPublicKey(),
                nodeA.getTransactionInputs(accountA.getPublicKey())
        );

        nodeA.onReceiveTransaction(tx);

        boolean propagated = waitUntil(
                () -> nodeC.getTransactionPool().contains(tx),
                60000
        );

        Assertions.assertTrue(propagated,
                "Transaction should propagate to Node C");

        boolean mined = nodeB.mineCurrentBlock(15000);
        Assertions.assertTrue(mined);

        nodeB.broadcastLastBlock();

        boolean consensus = waitUntil(
                () -> nodeA.getCurrentBlockIdx() == nodeB.getCurrentBlockIdx()
                        && nodeC.getCurrentBlockIdx() == nodeB.getCurrentBlockIdx(),
                60000
        );

        Assertions.assertTrue(consensus,
                "Consensus should be reached");

        Assertions.assertArrayEquals(nodeA.getHash(), nodeB.getHash());
        Assertions.assertArrayEquals(nodeB.getHash(), nodeC.getHash());

        nodeA.shutdown();
        nodeB.shutdown();
        nodeC.shutdown();
    }



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

    private int randomPort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

}
