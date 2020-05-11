package org.nting.flare.java;

import java.util.Map;

public interface StreamReader {

    // Instantiate the right type of Reader based on the input values
    static StreamReader createStreamReader(Object data) {
        StreamReader reader = null;
        if (data instanceof byte[]) {
            reader = new BlockReader((byte[]) data);
            // Move the readIndex forward for the binary reader.
            reader.readUint8("F");
            reader.readUint8("L");
            reader.readUint8("A");
            reader.readUint8("R");
            reader.readUint8("E");
        } else if (data instanceof Map) {
            reader = new JSONBlockReader((Map) data);
        }
        return reader;
    }

    int blockType();

    boolean isEOF();

    int readUint8Length();

    int readUint16Length();

    long readUint32Length();

    int readUint8(String label);

    int[] readUint8Array(int length, String label);

    int readInt8(String label);

    int readUint16(String label);

    int[] readUint16Array(int length, String label);

    int readInt16(String label);

    int readInt32(String label);

    long readUint32(String label);

    int readVersion();

    float readFloat32(String label);

    float[] readFloat32Array(int length, String label);

    double readFloat64(String label);

    String readString(String label);

    boolean readBoolean(String label);

    int readId(String label);

    StreamReader readNextBlock(Map<String, Integer> types);

    void openArray(String label);

    void closeArray();

    void openObject(String label);

    void closeObject();

    String containerType();

    byte[] readAsset();
}
