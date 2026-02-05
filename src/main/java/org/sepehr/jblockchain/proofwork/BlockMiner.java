package org.sepehr.jblockchain.proofwork;

import org.sepehr.jblockchain.timestampserver.Block;

public interface BlockMiner {

    boolean mine(final Block block, long maxTime);

}
