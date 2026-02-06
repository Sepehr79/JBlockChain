package org.sepehr.jblockchain.proofwork;

import lombok.Getter;
import org.sepehr.jblockchain.timestampserver.Block;

@Getter
public class SimpleBlockMiner implements BlockMiner {

    private final int difficulty;

    public SimpleBlockMiner(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public boolean mine(Block block, long maxTime) {
        long startTime = System.currentTimeMillis();
        boolean timeout = false;
        while (!verifyBlock(block) && !timeout) {
            block.increaseNonce();
            timeout = (System.currentTimeMillis() - startTime) > maxTime;
        }
        return true;
    }

    @Override
    public boolean verifyBlock(Block block) {
        byte[] bytes = block.getHash();
        for (int i = 0; i < difficulty; i++) {
            if (bytes[i] != 0)
                return false;
        }
        return true;
    }
}
