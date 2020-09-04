package org.nting.flare.java;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class JSONReader implements StreamReader {

    private Object _readObject;
    private LinkedList _context;

    public JSONReader(Map object) {
        _readObject = object.get("container");
        _context = new LinkedList<>();
        _context.addFirst(_readObject);
    }

    private <T> T readProp(String label) {
        Object head = _context.get(0);
        if (head instanceof AbstractMap) {
            Object prop = ((Map) head).get(label);
            ((Map) head).remove(label);
            return (T) prop;
        } else if (head instanceof AbstractList) {
            Object prop = ((List) head).remove(0);
            return (T) prop;
        }
        return null;
    }

    @Override
    public float readFloat32(String label) {
        Number f = readProp(label);
        return Optional.ofNullable(f).map(Number::floatValue).orElse(0.0f);
    }

    // Reads the array into ar
    @Override
    public float[] readFloat32Array(int length, String label) {
        float[] ar = new float[length];
        List<Number> list = readProp(label);
        if (list != null) {
            for (int i = 0; i < ar.length; i++) {
                ar[i] = list.get(i).floatValue();
            }
        }
        return ar;
    }

    @Override
    public double readFloat64(String label) {
        Number f = readProp(label);
        return Optional.ofNullable(f).map(Number::doubleValue).orElse(0.0);
    }

    @Override
    public int readUint8(String label) {
        return Optional.ofNullable(this.<Integer> readProp(label)).orElse(0);
    }

    @Override
    public int readUint8Length() {
        return _readLength();
    }

    @Override
    public boolean isEOF() {
        return _context.size() <= 1 && ((_readObject instanceof AbstractMap && ((Map) _readObject).size() == 0)
                || (_readObject instanceof AbstractList && ((List) _readObject).size() == 0));
    }

    @Override
    public int readInt8(String label) {
        return Optional.ofNullable(this.<Integer> readProp(label)).orElse(0);
    }

    @Override
    public int readUint16(String label) {
        return Optional.ofNullable(this.<Integer> readProp(label)).orElse(0);
    }

    @Override
    public int[] readUint8Array(int length, String label) {
        int[] ar = new int[length];
        List<Integer> list = readProp(label);
        if (list != null) {
            for (int i = 0; i < ar.length; i++) {
                ar[i] = list.get(i);
            }
        }
        return ar;
    }

    @Override
    public int[] readUint16Array(int length, String label) {
        int[] ar = new int[length];
        List<Integer> list = readProp(label);
        if (list != null) {
            for (int i = 0; i < ar.length; i++) {
                ar[i] = list.get(i);
            }
        }
        return ar;
    }

    @Override
    public int readInt16(String label) {
        return Optional.ofNullable(this.<Integer> readProp(label)).orElse(0);
    }

    @Override
    public int readUint16Length() {
        return _readLength();
    }

    @Override
    public long readUint32Length() {
        return _readLength();
    }

    @Override
    public long readUint32(String label) {
        return Optional.ofNullable(this.<Integer> readProp(label)).orElse(0);
    }

    @Override
    public int readInt32(String label) {
        return Optional.ofNullable(this.<Integer> readProp(label)).orElse(0);
    }

    @Override
    public int readVersion() {
        return Optional.ofNullable(this.<Integer> readProp("version")).orElse(0);
    }

    @Override
    public String readString(String label) {
        return Optional.ofNullable(this.<String> readProp(label)).orElse("");
    }

    @Override
    public boolean readBoolean(String label) {
        return Optional.ofNullable(this.<Boolean> readProp(label)).orElse(false);
    }

    // @hasOffset flag is needed for older (up until version 14) files.
    // Since the JSON Reader has been added in version 15, the field
    // here is optional.
    @Override
    public int readId(String label) {
        Integer val = readProp(label);
        return val != null ? val + 1 : 0;
    }

    @Override
    public void openArray(String label) {
        Object array = readProp(label);
        _context.addFirst(array);
    }

    @Override
    public void closeArray() {
        _context.removeFirst();
    }

    @Override
    public void openObject(String label) {
        Object o = readProp(label);
        _context.addFirst(o);
    }

    @Override
    public void closeObject() {
        _context.removeFirst();
    }

    private int _readLength() {
        if (_context.get(0) instanceof AbstractList) {
            return ((List) _context.get(0)).size();
        } else if (_context.get(0) instanceof AbstractMap) {
            return ((Map) _context.get(0)).size();
        }
        return 0;
    }

    @Override
    public byte[] readAsset() {
        String encodedAsset = readString("data"); // are we sure we need a label here?
        return Base64.getDecoder().decode(encodedAsset.substring(0, 22)); // ??? "Base64Decoder().convert(encodedAsset,
                                                                          // 22)"
    }

    @Override
    public String containerType() {
        return "json";
    }

    public LinkedList context() {
        return _context;
    }
}
