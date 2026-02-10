package org.sepehr.jblockchain;

import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.account.DataEncoder;
import org.sepehr.jblockchain.network.DistributedTimestampServer;
import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ExecutionException, InterruptedException {

        if (args.length != 3) {
            System.out.println(
                    "Usage: java NodeMain <difficulty> <port> <max supply>"
            );
            System.exit(1);
        }

        int difficulty = Integer.parseInt(args[0]);
        int port       = Integer.parseInt(args[1]);
        int maxSupply  = Integer.parseInt(args[2]);


        String privateKeyString = Files.readString(Path.of("private.key"));
        String publicKeyString  = Files.readString(Path.of("public.key"));

        PublicKey publicKey = DataEncoder.getInstance().decodePublicKey(publicKeyString);
        PrivateKey privateKey = DataEncoder.getInstance().decodePrivateKey(privateKeyString);
        Account account = new Account(privateKey, publicKey);

        // ---- start server ----
        DistributedTimestampServer server =
                new DistributedTimestampServer(
                        account,
                        maxSupply,
                        new SimpleBlockMiner(difficulty),
                        port
                );

        Thread thread = new Thread(server);
        thread.start();
        thread.join();

    }
}

