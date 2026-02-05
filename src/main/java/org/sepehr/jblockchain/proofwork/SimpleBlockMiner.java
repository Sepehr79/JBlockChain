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
        while (!satisfyMineCondition(block.getHash()) && !timeout) {
            block.increaseNonce();
            timeout = (System.currentTimeMillis() - startTime) > maxTime;
        }
        return true;
    }

    private boolean satisfyMineCondition(byte[] bytes) {
        for (int i = 0; i < difficulty; i++) {
            if (bytes[i] != 0)
                return false;
        }
        return true;
    }
}
