package org.sepehr.jblockchain.proofwork;

import lombok.Getter;
import lombok.Setter;
import org.sepehr.jblockchain.timestampserver.Block;

@Getter
@Setter
public class SimpleBlockMiner implements BlockMiner {

    private int difficulty;

    private static SimpleBlockMiner miner;

    private SimpleBlockMiner(int difficulty) {
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

    public static SimpleBlockMiner getInstance() {
        if (miner == null)
            miner = new SimpleBlockMiner(2);
        return miner;
    }
}
