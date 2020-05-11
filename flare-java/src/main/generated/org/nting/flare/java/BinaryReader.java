package org.nting.flare.java;

/// Implementation of StreamReader that reads binary data.
public abstract class BinaryReader implements StreamReader {

    private byte[] _raw;
    private int _readIndex;

    public BinaryReader(byte[] data) {
        _raw = data;
        _readIndex = 0;
    }

    public byte[] raw() {
        return _raw;
    }

    public int readIndex() {
        return _readIndex;
    }

    public byte[] readBytes(int length) {
        byte[] value = new byte[length];
        for (int i = 0; i < length; i++) {
            value[i] = _raw[_readIndex++];
        }
        return value;
    }

    @Override
    public float readFloat32(String label) {
        float value = Float.intBitsToFloat(getIntLittleEndian(_raw, _readIndex));
        _readIndex += 4;

        return value;
    }

    @Override
    public float[] readFloat32Array(int length, String label) {
        float[] list = new float[length];
        for (int i = 0; i < length; i++) {
            list[i] = readFloat32("");
        }
        return list;
    }

    @Override
    public double readFloat64(String label) {
        double value = Double.longBitsToDouble(getLongLittleEndian(_raw, _readIndex));
        _readIndex += 8;

        return value;
    }

    @Override
    public int readUint8(String label) {
        return _raw[_readIndex++] & 0xff;
    }

    @Override
    public boolean isEOF() {
        return _readIndex >= _raw.length;
    }

    @Override
    public int readInt8(String label) {
        return _raw[_readIndex++];
    }

    @Override
    public int readUint16(String label) {
        int value = makeInt((byte) 0, (byte) 0, _raw[_readIndex + 1], _raw[_readIndex]);
        _readIndex += 2;

        return value;
    }

    @Override
    public int[] readUint16Array(int length, String label) {
        int[] value = new int[length];
        for (int i = 0; i < length; i++) {
            value[i] = readUint16("");
        }
        return value;
    }

    @Override
    public int readInt16(String label) {
        int value = getShortLittleEndian(_raw, _readIndex);
        _readIndex += 2;

        return value;
    }

    @Override
    public long readUint32(String label) {
        long value = makeLong((byte) 0, (byte) 0, (byte) 0, (byte) 0, _raw[_readIndex + 3], _raw[_readIndex + 2],
                _raw[_readIndex + 1], _raw[_readIndex]);
        _readIndex += 4;

        return value;
    }

    @Override
    public int readInt32(String label) {
        int value = getIntLittleEndian(_raw, _readIndex);
        _readIndex += 4;

        return value;
    }

    @Override
    public String readString(String label) {
        int length = readInt32("");
        int end = _readIndex + length;
        StringBuilder stringBuilder = new StringBuilder();

        while (_readIndex < end) {
            int c1 = readUint8("");
            if (c1 < 128) {
                stringBuilder.append((char) c1);
            } else if (c1 > 191 && c1 < 224) {
                int c2 = readUint8("");
                stringBuilder.append((char) ((c1 & 31) << 6 | c2 & 63));
            } else if (c1 > 239 && c1 < 365) {
                int c2 = readUint8("");
                int c3 = readUint8("");
                int c4 = readUint8("");
                int u = ((c1 & 7) << 18 | (c2 & 63) << 12 | (c3 & 63) << 6 | c4 & 63) - 0x10000;
                stringBuilder.append((char) (0xD800 + (u >> 10)));
                stringBuilder.append((char) (0xDC00 + (u & 1023)));
            } else {
                int c2 = readUint8("");
                int c3 = readUint8("");
                stringBuilder.append((char) ((c1 & 15) << 12 | (c2 & 63) << 6 | c3 & 63));
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public int[] readUint8Array(int length, String label) {
        int[] value = new int[length];
        for (int i = 0; i < length; i++) {
            value[i] = readUint8("");
        }
        return value;
    }

    @Override
    public int readVersion() {
        return readInt32("");
    }

    @Override
    public int readUint8Length() {
        return readUint8("");
    }

    @Override
    public long readUint32Length() {
        return readUint32("");
    }

    @Override
    public int readUint16Length() {
        return readUint16("");
    }

    @Override
    public int readId(String label) {
        return readUint16(label);
    }

    @Override
    public boolean readBoolean(String label) {
        return readUint8(label) == 1;
    }

    @Override
    public byte[] readAsset() {
        int length = readInt32("");
        return readBytes(length);
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
    public String containerType() {
        return "bin";
    }

    private static long getLongLittleEndian(byte[] bb, int bi) {
        return makeLong(bb[bi + 7], bb[bi + 6], bb[bi + 5], bb[bi + 4], bb[bi + 3], bb[bi + 2], bb[bi + 1], bb[bi]);
    }

    private static long makeLong(byte b7, byte b6, byte b5, byte b4, byte b3, byte b2, byte b1, byte b0) {
        return ((((long) b7) << 56) | //
                (((long) b6 & 0xff) << 48) | //
                (((long) b5 & 0xff) << 40) | //
                (((long) b4 & 0xff) << 32) | //
                (((long) b3 & 0xff) << 24) | //
                (((long) b2 & 0xff) << 16) | //
                (((long) b1 & 0xff) << 8) | //
                (((long) b0 & 0xff)));
    }

    private static int getIntLittleEndian(byte[] bb, int bi) {
        return makeInt(bb[bi + 3], bb[bi + 2], bb[bi + 1], bb[bi]);
    }

    private static int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3) << 24) | //
                ((b2 & 0xff) << 16) | //
                ((b1 & 0xff) << 8) | //
                ((b0 & 0xff)));
    }

    private static short getShortLittleEndian(byte[] bb, int bi) {
        return makeShort(bb[bi + 1], bb[bi]);
    }

    private static short makeShort(byte b1, byte b0) {
        return (short) ((b1 << 8) | (b0 & 0xff));
    }
}
