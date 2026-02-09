package org.sepehr.jblockchain.network;

import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;
import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.timestampserver.SimpleTimestampServer;
import org.sepehr.jblockchain.transaction.Transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DistributedTimestampServer extends SimpleTimestampServer implements ConnectionGate {

    private final int port;
    private final List<Integer> peerPorts;
    private final Set<String> seenMessages = Collections.synchronizedSet(new HashSet<>());

    public DistributedTimestampServer(Account baseAccount, long maxSupply, int port, List<Integer> peerPorts) {
        super(baseAccount, maxSupply);
        this.port = port;
        this.peerPorts = peerPorts;
        startNetworkListener();
    }

    private void startNetworkListener() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Node started on port: " + port);
                while (!Thread.currentThread().isInterrupted()) {
                    try (Socket clientSocket = serverSocket.accept();
                         ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                        Object receivedData = in.readObject();

                        if (receivedData instanceof Transaction) {
                            onReceiveTransaction((Transaction) receivedData);
                        } else if (receivedData instanceof Block) {
                            onReceiveBlock((Block) receivedData);
                        }
                    } catch (Exception e) {
                        System.err.println("Error receiving data: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onReceiveTransaction(Transaction transaction) {
        String txHash = Base64.getEncoder().encodeToString(transaction.getHash());

        if (seenMessages.contains(txHash)) return;
        seenMessages.add(txHash);

        if (this.appendTransaction(transaction)) {
            System.out.println("[" + port + "] New transaction received and relayed.");
            broadcastTransaction(transaction);
        }
    }

    @Override
    public void onReceiveBlock(Block block) {
        String blockHash = Base64.getEncoder().encodeToString(block.getHash());

        if (seenMessages.contains(blockHash)) return;
        seenMessages.add(blockHash);

        if (this.acceptBlock(block)) {
            System.out.println("[" + port + "] New block accepted: Index " + block.getIdx());

            SimpleBlockMiner.getInstance().stopCurrentMining();

            broadcastBlock(block);
        }
    }

    @Override
    public boolean appendTransaction(Transaction transaction) {
        if (super.appendTransaction(transaction)) {
            broadcastTransaction(transaction);
            return true;
        }
        return false;
    }

    @Override
    public boolean mineCurrentBlock(long timeout) {
        if (super.mineCurrentBlock(timeout)) {
            broadcastBlock(super.getBlocks().get(this.getBlocks().size() - 1));
            return true;
        }
        return false;
    }

    @Override
    public void broadcastTransaction(Transaction transaction) {
        sendToPeers(transaction);
    }

    @Override
    public void broadcastBlock(Block block) {
        sendToPeers(block);
    }

    private void sendToPeers(Object data) {
        for (int peerPort : peerPorts) {
            if (peerPort == this.port) continue;

            new Thread(() -> {
                try (Socket socket = new Socket("localhost", peerPort);
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                    out.writeObject(data);
                    out.flush();
                } catch (IOException ignored) {
                    System.out.println("Peer not available-> localhost:" + peerPort);
                }
            }).start();
        }
    }
}