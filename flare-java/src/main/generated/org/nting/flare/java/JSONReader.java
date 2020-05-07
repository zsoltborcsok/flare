package org.nting.flare.java;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class JSONReader implements StreamReader {
  @Override
  public int blockType;

  private Object _readObject;
  private ListQueue _context;

  JSONReader(Map object) {
    _readObject = object["container"];
    _context = ListQueue<Object>();
    _context.addFirst(_readObject);
  }

  private <T> T readProp(String label) {
    Object head = _context.get(0);
    if (head instanceof Map) {
      Object prop = head[label];
      head.remove(label);
      if (prop instanceof T) {
        return prop;
      } else {
        return null;
      }
    } else if (head instanceof List) {
      Object prop = head.removeAt(0);
      if (prop instanceof T) {
        return prop;
      } else {
        return null;
      }
    }
    return null;
  }

  @Override
  public float readFloat32(String label) {
    Float f = readProp(label);
    return Optional.ofNullable(f).orElse(0.0f);
  }

  // Reads the array into ar
  @Override
  public float[] readFloat32Array(int length, String label) {
    var ar = new Float32List(length);
    _readArray(ar, label);
    return ar;
  }

  public void _readArray(List ar, String label) {
    List array = readProp<List>(label);
    if (array == null) {
      return;
    }
    for (int i = 0; i < ar.size(); i++) {
      num val = array[i] as num;
      ar[i] = ar.get(0) instanceof double ? val.toDouble() : val.toInt();
    }
  }

  @Override
  public double readFloat64(String label) {
    Double f = readProp(label);
    return Optional.ofNullable(f).orElse(0.0);
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
    return _context.size() <= 1 && _readObject.size() == 0;
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
  public byte[] readUint8Array(int length, String label) {
    var ar = new byte[length];
    _readArray(ar, label);
    return ar;
  }

  @Override
  public Uint16List readUint16Array(int length, String label) {
    var ar = new Uint16List(length);
    _readArray(ar, label);
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
  public int readUint32Length() {
    return _readLength();
  }

  @Override
  public int readUint32(String label) {
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
    var val = readProp<num>(label);
    return val != null ? val.toInt() + 1 : 0;
  }

  @Override
  public void openArray(String label) {
    Object array = readProp<Object>(label);
    _context.addFirst(array);
  }

  @Override
  public void closeArray() {
    _context.removeFirst();
  }

  @Override
  public void openObject(String label) {
    Object o = readProp<Object>(label);
    _context.addFirst(o);
  }

  @Override
  public void closeObject() {
    _context.removeFirst();
  }

  public int _readLength() {
    if (_context.get(0) instanceof List) {
      return ((List) _context.get(0)).size();
    } else if (_context.get(0) instanceof Map) {
      return ((Map) _context.get(0)).size();
    }
    return 0;
  }

  @Override
  public byte[] readAsset() {
    String encodedAsset =
    readString("data"); // are we sure we need a label here?
    return final Base64Decoder().convert(encodedAsset, 22);
  }

  @Override
  public String containerType() { return "json"; }

  public ListQueue context() { return _context; }
}
