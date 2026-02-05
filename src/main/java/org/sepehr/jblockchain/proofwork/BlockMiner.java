package org.sepehr.jblockchain.proofwork;

import org.sepehr.jblockchain.timestampserver.Block;

public interface BlockMiner {

    boolean mine(Block block, long maxTime);

    boolean verifyBlock(Block block);

}
