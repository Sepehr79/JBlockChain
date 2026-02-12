package org.sepehr.jitcoin.network;

import lombok.Getter;
import org.sepehr.jitcoin.account.Account;
import org.sepehr.jitcoin.proofwork.BlockMiner;
import org.sepehr.jitcoin.request.FollowerNodeReply;
import org.sepehr.jitcoin.request.FollowerNodeRequest;
import org.sepehr.jitcoin.request.TransactionInputReply;
import org.sepehr.jitcoin.request.TransactionInputRequest;
import org.sepehr.jitcoin.timestampserver.Block;
import org.sepehr.jitcoin.timestampserver.SimpleTimestampServer;
import org.sepehr.jitcoin.transaction.Transaction;
import org.sepehr.jitcoin.transaction.Utxo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistributedTimestampServer
        extends SimpleTimestampServer
        implements ConnectionGate, Runnable {

    private final int port;
    private final Set<String> peers = new HashSet<>();

    private ServerSocket serverSocket;
    private volatile boolean running = true;

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
            int port) {

        super(baseAccount, maxSupply, blockMiner);
        this.port = port;
        startNetworkListener();
    }

    public DistributedTimestampServer(List<Block> blocks, Set<Utxo> utxoSet, BlockMiner blockMiner, int port) {
        super(blocks, utxoSet, blockMiner);
        this.port = port;
        startNetworkListener();
    }

    private void startNetworkListener() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Node started on port: " + port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server on port " + port, e);
        }

        networkExecutor.submit(() -> {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    networkExecutor.submit(() -> handleConnection(socket));
                } catch (IOException e) {
                    if (!running) break;
                }
            }
        });
    }

    private void handleConnection(Socket socket) {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Object data;
            while ((data = in.readObject()) != null) {

                if (data instanceof Transaction) {
                    Transaction tx = (Transaction) data;
                    onReceiveTransaction(tx);

                } else if (data instanceof Block) {
                    Block block = (Block) data;
                    onReceiveBlock(block);

                } else if (data instanceof TransactionInputRequest) {
                    TransactionInputRequest request = (TransactionInputRequest) data;
                    List<Utxo> inputs = getTransactionInputs(request.getAccount());
                    TransactionInputReply reply = new TransactionInputReply(inputs);

                    try {
                        out.writeObject(reply);
                        out.flush();
                    } catch (IOException e) {
                        System.err.println("Failed to send TransactionInputReply: " + e.getMessage());
                    }

                } else if (data instanceof FollowerNodeRequest) {
                    String address = ((FollowerNodeRequest) data).getAddress();
                    this.addPeer(address);

                    FollowerNodeReply followerNodeReply = new FollowerNodeReply(this.getBlocks(), this.getUtxoSet(), this.getBlockMiner().getDifficulty());
                    out.writeObject(followerNodeReply);
                    out.flush();
                } else {
                    System.err.println("Unknown request type: " + data.getClass());
                }
            }

        } catch (IOException ignored) {

        } catch (ClassNotFoundException e) {
            System.err.println("Invalid object received: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                socket.close();
            } catch (IOException ignored) {}
        }
    }


    @Override
    public void onReceiveTransaction(Transaction tx) {

        String hash = Base64.getEncoder().encodeToString(tx.getHash());
        boolean isNew = seenMessages.add(hash);

        boolean appended = appendTransaction(tx);

        // 🔥 این خط کلیدی است
        transactionPool.add(tx);

        if (isNew) {
            broadcastTransaction(tx);
        }

        if (appended) {
            System.out.println("[" + port + "] TX appended");
        }
    }


    @Override
    public void onReceiveBlock(Block block) {

        String hash = Base64.getEncoder().encodeToString(block.getHash());

        boolean isNew = seenMessages.add(hash);

        if (acceptBlock(block)) {
            block.getItems().forEach(transactionPool::remove);

            if (isNew) {
                broadcastBlock(block);
            }

            System.out.println("[" + port + "] Block accepted: " + block.getIdx());
        }
    }


    // ================= BROADCAST =================

    @Override
    public void broadcastTransaction(Transaction tx) {
        String hash = Base64.getEncoder().encodeToString(tx.getHash());
        seenMessages.add(hash);
        sendToPeers(tx);
    }

    @Override
    public void broadcastBlock(Block block) {
        String hash = Base64.getEncoder().encodeToString(block.getHash());
        seenMessages.add(hash);
        sendToPeers(block);
    }

    @Override
    public void addPeer(String peer) {
        this.peers.add(peer);
    }

    public void addPeers(String ...peers) {
        this.peers.addAll(Arrays.asList(peers));
    }

    private void sendToPeers(Object data) {
        for (String peer : peers) {
            networkExecutor.submit(() -> {

                String[] address = peer.split(":");
                int attempts = 5;

                while (attempts-- > 0) {
                    try (Socket socket =
                                 new Socket(address[0], Integer.parseInt(address[1]));
                         ObjectOutputStream out =
                                 new ObjectOutputStream(socket.getOutputStream())) {

                        out.writeObject(data);
                        out.flush();
                        return;

                    } catch (IOException e) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {}
                    }
                }

                System.out.println("Failed after retries: " + peer);
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

    public void shutdown() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}

        networkExecutor.shutdownNow();
    }

}
