package org.sepehr.jblockchain;

import org.sepehr.jblockchain.block.Block;
import org.sepehr.jblockchain.chain.BlockChain;
import org.sepehr.jblockchain.sample.Transaction;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Block.BlockBuilder firstBlock = Block.builder()
                .blockHeaders(Map.of("Description", "This block contains transactions between two people"))
                .blockBodies(
                        List.of(new Transaction("Kian", "Kaveh", "1.5"),
                                new Transaction("Kamran", "Kiarash", "2.25"))
                );
        Block.BlockBuilder secondBlock = Block.builder()
                .blockHeaders(Map.of("Description", "This block contains messages between two people"))
                .blockBodies(
                        List.of(new Transaction("Homa", "Hashem", "Hello Hashem"),
                                new Transaction("Hamed", "Hamid", "Nice to meet you Hamid"))
                );


        final var blockChain = new BlockChain();
        blockChain.addBlock(firstBlock);
        blockChain.addBlock(secondBlock);
    }
}
