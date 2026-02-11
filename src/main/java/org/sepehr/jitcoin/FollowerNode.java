package org.sepehr.jitcoin;

import org.sepehr.jitcoin.network.DistributedTimestampServer;
import org.sepehr.jitcoin.proofwork.SimpleBlockMiner;
import org.sepehr.jitcoin.request.FollowerNodeReply;
import org.sepehr.jitcoin.request.FollowerNodeRequest;
import org.sepehr.jitcoin.timestampserver.SimpleTimestampServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class FollowerNode {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(
                    "Usage: java -jar FollowerNode.jar <currentAddress> <remoteAddress>"
            );
            System.exit(1);
        }

        String currentAddress = (args[0]);
        String remoteAddress = (args[1]);

        try (Socket socket = new Socket(remoteAddress.split(":")[0], Integer.parseInt(remoteAddress.split(":")[1]))) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            FollowerNodeRequest followerNodeRequest = new FollowerNodeRequest(currentAddress);
            out.writeObject(followerNodeRequest);
            out.flush();

            FollowerNodeReply reply = (FollowerNodeReply) in.readObject();
            var server = new DistributedTimestampServer(reply.getBlocks(), reply.getOpenUtxos(),
                    new SimpleBlockMiner(reply.getMiningDifficulty()),
                    Integer.parseInt(currentAddress.split(":")[1]));
            server.addPeer(remoteAddress);
            Thread thread = new Thread(server);
            thread.start();
            System.out.println("Server start with current blocks size: " + server.getBlocks().size());
            thread.join();


        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
