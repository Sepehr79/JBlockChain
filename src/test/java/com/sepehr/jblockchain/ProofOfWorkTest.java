package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.proofwork.SimpleBlockMiner;
import org.sepehr.jblockchain.timestampserver.Block;

public class ProofOfWorkTest {

    @Test
    void mineBlockTest() {
        Block block = new Block("prevHash".getBytes(), 5);

        Assertions.assertTrue(SimpleBlockMiner.getInstance().mine(block, 10_000));
        Assertions.assertTrue(SimpleBlockMiner.getInstance().verifyBlock(block));
    }

}
