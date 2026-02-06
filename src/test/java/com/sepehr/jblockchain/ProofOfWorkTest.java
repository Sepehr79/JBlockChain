package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;
import org.sepehr.jblockchain.timestampserver.Block;

public class ProofOfWorkTest {

    @Test
    void mineBlockTest() {
        SimpleBlockMiner miner = new SimpleBlockMiner(2);
        Block block = new Block("prevHash".getBytes(), 5);

        Assertions.assertTrue(miner.mine(block, 10_000));
        Assertions.assertTrue(miner.verifyBlock(block));
    }

}
