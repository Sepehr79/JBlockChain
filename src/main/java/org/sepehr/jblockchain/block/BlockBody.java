package org.sepehr.jblockchain.block;

import com.google.common.hash.Hashing;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public interface BlockBody extends Serializable {
    /**
     * @return Hash value of the block body
     */
    default String hash() {
        return Hashing.sha256()
                .hashString(this.toString(), StandardCharsets.UTF_8)
                .toString();
    }
}
