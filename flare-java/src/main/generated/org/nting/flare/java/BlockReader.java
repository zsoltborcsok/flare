package org.nting.flare.java;

import java.util.Map;

public class BlockReader extends BinaryReader {

    public int blockType;

  BlockReader(byte[] data) : super(data) {
    blockType = 0;
  }

  BlockReader.fromBlock(this.blockType, byte[] stream) : super(stream);

  // A block is defined as a TLV with type of one byte, length of 4 bytes,
  // and then the value following.
    @Override
    public BlockReader readNextBlock(Map<String, Integer> types) {
        if (isEOF()) {
            return null;
        }
        int blockType = readUint8();
        int length = readUint32();
        return BlockReader.fromBlock(blockType, readBytes(length));
    }
}
