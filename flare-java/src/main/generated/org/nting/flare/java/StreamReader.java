package org.nting.flare.java;

public abstract class StreamReader {
  int blockType = 0;

  // Instantiate the right type of Reader based on the input values
  factory StreamReader(dynamic data) {
    StreamReader reader;
    if (data instanceof ByteData) {
      reader = new BlockReader(data);
      // Move the readIndex forward for the binary reader.
      reader.readUint8("F");
      reader.readUint8("L");
      reader.readUint8("A");
      reader.readUint8("R");
      reader.readUint8("E");
    } else if (data instanceof Map) {
      reader = new JSONBlockReader(data);
    }
    return reader;
  }

  public abstract boolean isEOF();

  public abstract int readUint8Length();

  public abstract int readUint16Length();

  public abstract int readUint32Length();

  public abstract int readUint8(String label);

  public abstract Uint8List readUint8Array(int length, String label);

  public abstract int readInt8(String label);

  public abstract int readUint16(String label);

  public abstract Uint16List readUint16Array(int length, String label);

  public abstract int readInt16(String label);

  public abstract int readInt32(String label);

  public abstract int readUint32(String label);

  public abstract int readVersion();

  public abstract double readFloat32(String label);

  public abstract Float32List readFloat32Array(int length, String label);

  public abstract double readFloat64(String label);

  public abstract String readString(String label);

  public abstract boolean readBoolean(String label);

  public abstract int readId(String label);

  public abstract StreamReader readNextBlock(Map<String, int> types);

  public abstract void openArray(String label);

  public abstract void closeArray();

  public abstract void openObject(String label);

  public abstract void closeObject();

  String get containerType;

  public abstract Uint8List readAsset();
}
