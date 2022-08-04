package org.sepehr.jblockchain.chain;

import org.sepehr.jblockchain.block.Block;

public class BlockChain {

    private final Block firstBlock;

    private Block currentBlock;

    public BlockChain() {
        this.firstBlock = new Block("00000000000000000000000000");
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

    public String currentHash() {
        return currentBlock.getCurrentHash();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        var block = getFirstBlock();

        while (block != null) {
            stringBuilder.append(block).append("\n");
            block = block.getNextBlock();
        }

        return stringBuilder.toString();
    }
}
