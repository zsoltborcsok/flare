package org.nting.flare.java;

import java.util.Map;

public class BlockReader extends BinaryReader {

    public int blockType;

    public BlockReader(byte[] data) {
        super(data);
        blockType = 0;
    }

    public BlockReader(int blockType, byte[] stream) {
        super(stream);
        this.blockType = blockType;
    }

    @Override
    public int blockType() {
        return blockType;
    }

    // A block is defined as a TLV with type of one byte, length of 4 bytes and then the value following.
    @Override
    public BlockReader readNextBlock(Map<String, Integer> types) {
        if (isEOF()) {
            return null;
        }
        int blockType = readUint8("");
        int length = (int) readUint32("");
        return new BlockReader(blockType, readBytes(length));
    }
}
