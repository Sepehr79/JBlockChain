package org.sepehr.jitcoin.network;

import lombok.Getter;
import org.sepehr.jitcoin.account.Account;
import org.sepehr.jitcoin.proofwork.BlockMiner;
import org.sepehr.jitcoin.timestampserver.Block;
import org.sepehr.jitcoin.timestampserver.SimpleTimestampServer;
import org.sepehr.jitcoin.transaction.Transaction;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistributedTimestampServer
        extends SimpleTimestampServer
        implements ConnectionGate, Runnable {

    private final int port;
    private List<String> peers = new ArrayList<>();

    private final Set<String> seenMessages =
            Collections.synchronizedSet(new HashSet<>());

    @Getter
    private final Set<Transaction> transactionPool =
            Collections.synchronizedSet(new HashSet<>());

    private final ExecutorService networkExecutor =
            Executors.newCachedThreadPool();

    public DistributedTimestampServer(
            Account baseAccount,
            long maxSupply,
            BlockMiner blockMiner,
            int port,
            List<String> peers) {

        super(baseAccount, maxSupply, blockMiner);
        this.port = port;
        this.peers = peers;
        startNetworkListener();
    }

    public DistributedTimestampServer(
            Account baseAccount,
            long maxSupply,
            BlockMiner blockMiner,
            int port) {

        super(baseAccount, maxSupply, blockMiner);
        this.port = port;
        startNetworkListener();
    }

    private void startNetworkListener() {
        networkExecutor.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Node started on port: " + port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    networkExecutor.submit(() -> handleConnection(socket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleConnection(Socket socket) {
        try (ObjectInputStream in =
                     new ObjectInputStream(socket.getInputStream())) {

            Object data = in.readObject();

            if (data instanceof Transaction) {
                onReceiveTransaction((Transaction) data);
            } else if (data instanceof Block) {
                onReceiveBlock((Block) data);
            }

        } catch (Exception e) {
            System.err.println("Receive error: " + e.getMessage());
        }
    }

    @Override
    public void onReceiveTransaction(Transaction tx) {
        String hash = Base64.getEncoder().encodeToString(tx.getHash());

        if (!seenMessages.add(hash)) return;

        if (appendTransaction(tx)) {
            transactionPool.add(tx);
            broadcastTransaction(tx);
            System.out.println("[" + port + "] TX received & broadcast");
        }
    }

    @Override
    public void onReceiveBlock(Block block) {
        String hash = Base64.getEncoder().encodeToString(block.getHash());

        if (!seenMessages.add(hash)) return;

        if (acceptBlock(block)) {
            block.getItems().forEach(transactionPool::remove);
            broadcastBlock(block);
            System.out.println("[" + port + "] Block accepted: " + block.getIdx());
        }
    }

    // ================= BROADCAST =================

    @Override
    public void broadcastTransaction(Transaction tx) {
        sendToPeers(tx);
    }

    @Override
    public void broadcastBlock(Block block) {
        sendToPeers(block);
    }

    private void sendToPeers(Object data) {
        for (String peer : peers) {

            networkExecutor.submit(() -> {
                String[] address = peer.split(":");
                try (Socket socket = new Socket(address[0], Integer.parseInt(address[1]));
                     ObjectOutputStream out =
                             new ObjectOutputStream(socket.getOutputStream())) {

                    out.writeObject(data);
                    out.flush();

                } catch (IOException e) {
                    System.out.println("Peer offline: " + peer);
                }
            });
        }
    }

    @Override
    public void run() {
        ExecutorService minerExecutor =
                Executors.newSingleThreadExecutor();

        int lastTxCount = 0;

        while (true) {
            int currentTxCount =
                    getCurrentBlock().getItems().size();

            if (currentTxCount > lastTxCount) {
                lastTxCount = currentTxCount;

                minerExecutor.submit(() -> {
                    if (mineCurrentBlock(Long.MAX_VALUE)) {
                        System.out.println(
                                "Block mined: " +
                                        getCurrentBlock().getIdx());
                        broadcastLastBlock();
                    }
                });
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }
    }

    public void broadcastLastBlock() {
        Block lastBlock =
                getBlocks().get(getBlocks().size() - 1);

        lastBlock.getItems().forEach(transactionPool::remove);
        sendToPeers(lastBlock);
    }
}
