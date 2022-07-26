package org.sepehr.jblockchain.chain;

import org.sepehr.jblockchain.block.Block;

public class BlockChain {

    private final Block firstBlock;

    private Block currentBlock;

    public BlockChain() {
        this.firstBlock = new Block("No hash");
        this.currentBlock = firstBlock;
    }

    public void addBlock(final Block.BlockBuilder blockBuilder) {
        Block newBlock = new Block(
                this.currentBlock.getCurrentHash(),
                blockBuilder.getBlockHeaders(),
                blockBuilder.getBlockBodies()
        );
        this.currentBlock.setNextBlock(newBlock);
        this.currentBlock = newBlock;
    }

    public Block getFirstBlock() {
        return firstBlock.getNextBlock();
    }

    public Block getCurrentBlock() {
        return currentBlock;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        var block = getFirstBlock();

        while (block != null) {
            stringBuilder
                    .append("Block id: ").append(block.getBlockId()).append("\n")
                    .append("Previous hash: ").append(block.getPreviousHash()).append("\n")
                    .append("Current hash: ").append(block.getCurrentHash()).append("\n")
                    .append("Block headers: ").append(block.getBlockHeaders()).append("\n")
                    .append("Block bodies: ").append(block.getBlockBodies()).append("\n\n");
            block = block.getNextBlock();
        }

        return stringBuilder.toString();
    }
}
