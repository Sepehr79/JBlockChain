package org.sepehr.jblockchain;

import org.sepehr.jblockchain.block.Block;
import org.sepehr.jblockchain.chain.BlockChain;
import org.sepehr.jblockchain.factory.imp.AccountFactoryImp;
import org.sepehr.jblockchain.factory.imp.KeyFactoryImp;
import org.sepehr.jblockchain.factory.imp.RecoveryWordsFactoryImp;
import org.sepehr.jblockchain.sample.Account;
import org.sepehr.jblockchain.sample.Message;
import org.sepehr.jblockchain.sample.Text;
import org.sepehr.jblockchain.sample.Transaction;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final AccountFactoryImp accountFactoryImp = new AccountFactoryImp(new KeyFactoryImp(), new RecoveryWordsFactoryImp());
        Account account1 = accountFactoryImp.buildAccount();
        Account account2 = accountFactoryImp.buildAccount();
        Account account3 = accountFactoryImp.buildAccount();
        Account account4 = accountFactoryImp.buildAccount();


        Block.BlockBuilder firstBlock = Block.builder()
                .blockHeaders(Map.of("Description", "This block contains transactions between two people"))
                .blockBodies(
                        List.of(new Transaction(account1.getPublicKey() /*Public key is address of account*/, account2.getPublicKey(), "1.5"),
                                new Transaction(account3.getPublicKey(), account4.getPublicKey(), "2.25"))
                );
        Block.BlockBuilder secondBlock = Block.builder()
                .blockHeaders(Map.of("Description", "This block contains messages between two people"))
                .blockBodies(
                        List.of(new Message("Homa", "Hashem", "Hello Hashem"),
                                new Message("Hamed", "Hamid", "Nice to meet you Hamid"))
                );
        Block.BlockBuilder thirdBlock = Block.builder()
                .blockHeaders(Map.of("description", "This block contains texts"))
                .blockBodies(List.of(new Text("Corona virus is a bad virus"),
                                     new Text("I have infected by the corona virus")));


        final var blockChain = new BlockChain();
        blockChain.addBlock(firstBlock);
        blockChain.addBlock(secondBlock);
        blockChain.addBlock(thirdBlock);

        System.out.println(blockChain);
    }
}
