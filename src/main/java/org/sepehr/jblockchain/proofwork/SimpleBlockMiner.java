package org.sepehr.jblockchain.proofwork;

import lombok.Getter;
import lombok.Setter;
import org.sepehr.jblockchain.timestampserver.Block;

@Getter
@Setter
public class SimpleBlockMiner implements BlockMiner {

    private int difficulty;
    private static SimpleBlockMiner miner;

    private volatile boolean mining;

    public SimpleBlockMiner(int difficulty) {
        this.difficulty = difficulty;
        this.mining = false;
    }

    @Override
    public boolean mine(Block block, long maxTime) {
        long startTime = System.currentTimeMillis();
        this.mining = true;

        while (!verifyBlock(block)) {
            if ((System.currentTimeMillis() - startTime) > maxTime || !mining) {
                this.mining = false;
                return false;
            }
            block.increaseNonce();
        }

        this.mining = false;
        return true;
    }

    @Override
    public boolean verifyBlock(Block block) {
        byte[] hash = block.getHash();
        if (hash == null || hash.length < difficulty) return false;

        for (int i = 0; i < difficulty; i++) {
            if (hash[i] != 0)
                return false;
        }
        return true;
    }

    public void stopCurrentMining() {
        this.mining = false;
    }
}