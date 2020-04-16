package org.nting.flare.java;

/// Implementation of StreamReader that reads binary data.
public abstract class BinaryReader implements StreamReader {
  ByteData _raw;
  int _readIndex;

  public ByteData raw() { return _raw; }

  public int readIndex() { return _readIndex; }

  BinaryReader(ByteData data) {
    _raw = data;
    _readIndex = 0;
  }

  public ByteData readBytes(int length) {
    int offset = _readIndex + raw.offsetInBytes;
    _readIndex += length;
    return raw.buffer.asByteData(offset, length);
  }

  @Override
  public double readFloat32([String label]) {
    double value = _raw.getFloat32(_readIndex, Endian.little);
    _readIndex += 4;

    return value;
  }

  @Override
  public double readFloat64([String label]) {
    double value = _raw.getFloat64(_readIndex, Endian.little);
    _readIndex += 8;

    return value;
  }

  @Override
  public int readUint8([String label]) {
    return _raw.getUint8(_readIndex++);
  }

  @Override
  public boolean isEOF() {
    return _readIndex >= _raw.lengthInBytes;
  }

  @Override
  public int readInt8([String label]) {
    return _raw.getInt8(_readIndex++);
  }

  @Override
  public int readUint16([String label]) {
    int value = _raw.getUint16(_readIndex, Endian.little);
    _readIndex += 2;

    return value;
  }

  @Override
  public Uint16List readUint16Array(int length, [String label]) {
    Uint16List list = new Uint16List(length);
    for (int i = 0; i < length; i++) {
      list[i] = _raw.getUint16(_readIndex, Endian.little);
      _readIndex += 2;
    }
    return list;
    // int offset = _readIndex;
    // _readIndex += length * 2;
    // // TODO: endianness?
    // return _raw.buffer.asUint16List(offset + _raw.offsetInBytes, length);
  }

  @Override
  public int readInt16([String label]) {
    int value = _raw.getInt16(_readIndex, Endian.little);
    _readIndex += 2;

    return value;
  }

  @Override
  public int readUint32([String label]) {
    int value = _raw.getUint32(_readIndex, Endian.little);
    _readIndex += 4;

    return value;
  }

  @Override
  public int readInt32([String label]) {
    int value = _raw.getInt32(_readIndex, Endian.little);
    _readIndex += 4;

    return value;
  }

  @Override
  public String readString([String label]) {
    int length = readUint32();
    int end = _readIndex + length;
    StringBuffer stringBuffer = new StringBuffer();

    while (_readIndex < end) {
      int c1 = readUint8();
      if (c1 < 128) {
        stringBuffer.writeCharCode(c1);
      } else if (c1 > 191 && c1 < 224) {
        int c2 = readUint8();
        stringBuffer.writeCharCode((c1 & 31) << 6 | c2 & 63);
      } else if (c1 > 239 && c1 < 365) {
        int c2 = readUint8();
        int c3 = readUint8();
        int c4 = readUint8();
        int u = ((c1 & 7) << 18 | (c2 & 63) << 12 | (c3 & 63) << 6 | c4 & 63) -
            0x10000;
        stringBuffer.writeCharCode(0xD800 + (u >> 10));
        stringBuffer.writeCharCode(0xDC00 + (u & 1023));
      } else {
        int c2 = readUint8();
        int c3 = readUint8();
        stringBuffer.writeCharCode((c1 & 15) << 12 | (c2 & 63) << 6 | c3 & 63);
      }
    }
    return stringBuffer.toString();
  }

  @Override
  public byte[] readUint8Array(int length, [String label]) {
    int offset = _readIndex + _raw.offsetInBytes;
    _readIndex += length;
    return _raw.buffer.asUint8List(offset, length);
  }

  @Override
  public int readVersion() {
    return readUint32();
  }

  @Override
  public int readUint8Length() {
    return readUint8();
  }

  @Override
  public int readUint32Length() {
    return readUint32();
  }

  @Override
  public int readUint16Length() {
    return readUint16();
  }

  @Override
  public int readId(String label) {
    return readUint16(label);
  }

  @Override
  public Float32List readFloat32Array(int length, String label) {
    Float32List list = new Float32List(length);
    for (int i = 0; i < length; i++) {
      list[i] = _raw.getFloat32(_readIndex, Endian.little);
      _readIndex += 4;
    }
    return list;
    //int offset = _readIndex;
    //return _raw.buffer.asFloat32List(offset+_raw.offsetInBytes, length);
  }

  @Override
  public boolean readBoolean(String label) {
    return readUint8(label) == 1;
  }

  @Override
  public byte[] readAsset() {
    int length = readUint32();
    return readUint8Array(length);
  }

  @Override
  public void openArray(String label) {
    /* NOP */
  }

  @Override
  public void closeArray() {
    /* NOP */
  }

  @Override
  public void openObject(String label) {
    /* NOP */
  }

  @Override
  public void closeObject() {
    /* NOP */
  }

  @Override
  public String containerType() { return "bin"; }
}
