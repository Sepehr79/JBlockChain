package org.sepehr.jblockchain.block;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Block {

    private final UUID blockId;

    private final Map<String, String> blockHeaders;

    private final List<BlockBody> blockBodies;

    private final String previousHash;

    private Block nextBlock;

    public Block(final String previousHash) {
        this(previousHash, new HashMap<>(), new ArrayList<>());
    }

    public Block(
            final String previousHash,
            final Map<String, String> blockHeaders,
            final List<BlockBody> blockBodies
    ) {
        this.blockId = UUID.randomUUID();
        this.previousHash = previousHash;
        this.blockHeaders = blockHeaders;
        this.blockBodies = blockBodies;
    }

    public String getCurrentHash() {
        return Hashing.sha256()
                .hashString(
                        blockId.toString() +
                        blockHeaders.toString() +
                        blockBodies.stream().map(BlockBody::hash).collect(Collectors.joining("")) +
                        previousHash,
                        StandardCharsets.UTF_8
                )
                .toString();
    }

    public UUID getBlockId() {
        return blockId;
    }

    public Map<String, String> getBlockHeaders() {
        return blockHeaders;
    }

    public List<BlockBody> getBlockBodies() {
        return blockBodies;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setNextBlock(Block nextBlock) {
        this.nextBlock = nextBlock;
    }

    public Block getNextBlock() {
        return nextBlock;
    }

    /**
     * Helper class to build a new block based on block headers and bodies
     */
    public static class BlockBuilder {

        private Map<String, String> blockHeaders;
        private List<BlockBody> blockBodies;

        public BlockBuilder() { }

        public BlockBuilder blockHeaders(final Map<String, String> blockHeaders) {
            this.blockHeaders = blockHeaders;
            return this;
        }

        public BlockBuilder blockBodies(List<BlockBody> blockBodies) {
            this.blockBodies = blockBodies;
            return this;
        }

        public Map<String, String> getBlockHeaders() {
            return blockHeaders;
        }

        public List<BlockBody> getBlockBodies() {
            return blockBodies;
        }
    }

    public static BlockBuilder builder() {
        return new BlockBuilder();
    }
}
