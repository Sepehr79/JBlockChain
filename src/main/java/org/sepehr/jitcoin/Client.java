package org.sepehr.jitcoin;

import org.sepehr.jitcoin.account.Account;
import org.sepehr.jitcoin.account.DataEncoder;
import org.sepehr.jitcoin.account.SimpleAccountFactory;
import org.sepehr.jitcoin.account.SimpleKeyFactory;
import org.sepehr.jitcoin.request.TransactionInputReply;
import org.sepehr.jitcoin.request.TransactionInputRequest;
import org.sepehr.jitcoin.transaction.SimpleTransactionClient;
import org.sepehr.jitcoin.transaction.Transaction;
import org.sepehr.jitcoin.transaction.Utxo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private static final List<Account> ACCOUNTS = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Load keys and initialize first account
        String privateKeyString = new String(Files.readAllBytes(Path.of("private.key")));
        String publicKeyString = new String(Files.readAllBytes(Path.of("public.key")));
        PublicKey publicKey = DataEncoder.getInstance().decodePublicKey(publicKeyString);
        PrivateKey privateKey = DataEncoder.getInstance().decodePrivateKey(privateKeyString);
        ACCOUNTS.add(new Account(privateKey, publicKey));

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter server address (Ip:Port): ");
        String[] serverAddress = scanner.nextLine().split(":");
        String host = serverAddress[0];
        int port = Integer.parseInt(serverAddress[1]);

        SimpleAccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());

        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            String input = "";
            while (!"exit".equals(input)) {
                System.out.println("Select: [1] Add new account, [2] Number of accounts, [3] Account inventory, [4] Create transaction or 'exit'");
                input = scanner.nextLine();

                if ("1".equals(input)) {
                    ACCOUNTS.add(accountFactory.buildAccount());
                    System.out.println("New account added.");

                } else if ("2".equals(input)) {
                    System.out.println("Number of accounts: " + ACCOUNTS.size());

                } else if ("3".equals(input)) {
                    System.out.print("Enter account number: ");
                    int accountNumber = Integer.parseInt(scanner.nextLine()) - 1;
                    Account selectedAccount = ACCOUNTS.get(accountNumber);

                    // Send request
                    TransactionInputRequest request = new TransactionInputRequest(selectedAccount.getPublicKey());
                    out.writeObject(request);
                    out.flush();

                    // Receive reply
                    Object response = in.readObject();
                    if (response instanceof TransactionInputReply) {
                        TransactionInputReply reply = (TransactionInputReply) response;
                        List<Utxo> utxos = reply.getInputs();

                        if (utxos == null || utxos.isEmpty()) {
                            System.out.println("No UTXOs available for this account.");
                        } else {
                            int sum = 0;
                            for (Utxo utxo : utxos) {
                                sum += utxo.getValue();
                            }
                            System.out.println("Inventory: " + sum);
                        }
                    } else {
                        System.out.println("Invalid response from server: " + response.getClass());
                    }

                } else if ("4".equals(input)) {
                    System.out.print("Enter sender number: ");
                    int accountNumber = Integer.parseInt(scanner.nextLine()) - 1;
                    Account sender = ACCOUNTS.get(accountNumber);

                    List<Utxo> inputs = getInputs(accountNumber, in, out);

                    if (inputs != null) {
                        System.out.print("Enter receiver number: ");
                        accountNumber = Integer.parseInt(scanner.nextLine()) - 1;
                        Account receiver = ACCOUNTS.get(accountNumber);

                        System.out.print("Enter sender amount: ");
                        long amount = Long.parseLong(scanner.nextLine());

                        SimpleTransactionClient simpleTransactionClient = new SimpleTransactionClient();
                        Transaction transaction = simpleTransactionClient.createTransaction(sender.getPublicKey(), sender.getPrivateKey(),
                                amount, receiver.getPublicKey(), inputs);
                        out.writeObject(transaction);
                        out.flush();
                    } else {
                        System.out.println("Not enough inventory");
                    }




                } else if (!"exit".equals(input)) {
                    System.out.println("Invalid input");
                }
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid response received: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
        }
    }

    private static List<Utxo> getInputs(int accountNumber, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        Account selectedAccount = ACCOUNTS.get(accountNumber);

        // Send request
        TransactionInputRequest request = new TransactionInputRequest(selectedAccount.getPublicKey());
        out.writeObject(request);
        out.flush();

        // Receive reply
        Object response = in.readObject();
        if (response instanceof TransactionInputReply) {
            TransactionInputReply reply = (TransactionInputReply) response;
            List<Utxo> utxos = reply.getInputs();
            return utxos;
        }
        return null;
    }
}
