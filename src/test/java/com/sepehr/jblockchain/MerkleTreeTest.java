package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.account.Account;
import org.sepehr.jblockchain.account.SimpleAccountFactory;
import org.sepehr.jblockchain.account.SimpleKeyFactory;
import org.sepehr.jblockchain.transaction.SimpleTransactionClient;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.TransactionInputFactory;
import org.sepehr.jblockchain.transaction.Utxo;
import org.sepehr.jblockchain.verification.MerkleTree;

import java.util.List;

public class MerkleTreeTest {

    @Test
    void testMerkleTreeCreationAndVerification() {
        SimpleAccountFactory accountFactory = new SimpleAccountFactory(new SimpleKeyFactory());
        Account account1 = accountFactory.buildAccount();
        Account account2 = accountFactory.buildAccount();
        Account account3 = accountFactory.buildAccount();

        var inputFactory = new TransactionInputFactory();
        List<Utxo> account1Input = inputFactory.createInput(account1.getPublicKey(), 500);
        List<Utxo> account2Input = inputFactory.createInput(account1.getPublicKey(), 700);
        List<Utxo> account3Input = inputFactory.createInput(account1.getPublicKey(), 300);

        SimpleTransactionClient transactionClient = new SimpleTransactionClient();
        Transaction transaction1 = transactionClient.createTransaction(
                account1.getPublicKey(),
                account1.getPrivateKey(),
                400,
                account2.getPublicKey(),
                account1Input
        );
        Transaction transaction2 = transactionClient.createTransaction(
                account2.getPublicKey(),
                account2.getPrivateKey(),
                100,
                account3.getPublicKey(),
                account2Input
        );
        Transaction transaction3 = transactionClient.createTransaction(
                account3.getPublicKey(),
                account3.getPrivateKey(),
                50,
                account1.getPublicKey(),
                account3Input
        );
        Transaction transaction4 = transactionClient.createTransaction(
                account3.getPublicKey(),
                account3.getPrivateKey(),
                51, // different value
                account1.getPublicKey(),
                account3Input
        );

        List<Transaction> transactions = List.of(transaction1, transaction2, transaction3);
        MerkleTree merkleTree = new MerkleTree(transactions);
        merkleTree.printTree();

        String root = merkleTree.getMerkleRoot();
        List<MerkleTree.ProofElement> proof = merkleTree.getProof(transaction2);
        Assertions.assertNull(merkleTree.getProof(transaction4));
        Assertions.assertTrue(merkleTree.verifyTransaction(transaction2, proof, root));

    }

}
