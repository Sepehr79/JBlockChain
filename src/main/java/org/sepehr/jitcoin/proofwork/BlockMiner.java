package org.sepehr.jitcoin.proofwork;

import org.sepehr.jitcoin.timestampserver.Block;

public interface BlockMiner {

    boolean mine(Block block, long maxTime);

    boolean verifyBlock(Block block);

    int getDifficulty();

}
