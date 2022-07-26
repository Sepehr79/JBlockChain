package org.sepehr.jblockchain.chain;

import org.sepehr.jblockchain.block.Block;

public class BlockChain {

    private final Block firstBlock;

    private Block currentBlock;

    public BlockChain() {
        this.firstBlock = new Block("FirstHash");
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
        return firstBlock;
    }

    public Block getCurrentBlock() {
        return currentBlock;
    }
}
