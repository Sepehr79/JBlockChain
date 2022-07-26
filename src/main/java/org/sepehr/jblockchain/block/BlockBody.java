package org.sepehr.jblockchain.block;

import java.io.Serializable;

public interface BlockBody extends Serializable {
    /**
     * @return Hash value of the block body
     */
    String hash();
}
