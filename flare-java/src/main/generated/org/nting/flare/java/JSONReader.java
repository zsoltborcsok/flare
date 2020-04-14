package org.nting.flare.java;

public abstract class JSONReader implements StreamReader {
  @Override
  int blockType;

  dynamic _readObject;
  ListQueue _context;

  JSONReader(Map object) {
    _readObject = object["container"];
    _context = ListQueue<dynamic>();
    _context.addFirst(_readObject);
  }

  T readProp<T>(String label) {
    dynamic head = _context.first;
    if (head instanceof Map) {
      dynamic prop = head[label];
      head.remove(label);
      if (prop instanceof T) {
        return prop;
      } else {
        return null;
      }
    } else if (head instanceof List) {
      dynamic prop = head.removeAt(0);
      if (prop instanceof T) {
        return prop;
      } else {
        return null;
      }
    }
    return null;
  }

  @Override
  public double readFloat32(String label) {
    num f = readProp<num>(label);
    return f?.toDouble() ?? 0.0;
  }

  // Reads the array into ar
  @Override
  public Float32List readFloat32Array(int length, String label) {
    var ar = new Float32List(length);
    _readArray(ar, label);
    return ar;
  }

  public void _readArray(List ar, String label) {
    List array = readProp<List>(label);
    if (array == null) {
      return;
    }
    for (int i = 0; i < ar.length; i++) {
      num val = array[i] as num;
      ar[i] = ar.first instanceof double ? val.toDouble() : val.toInt();
    }
  }

  @Override
  public double readFloat64(String label) {
    num f = readProp<num>(label);
    return f?.toDouble() ?? 0;
  }

  @Override
  public int readUint8(String label) {
    return readProp(label) ?? 0;
  }

  @Override
  public int readUint8Length() {
    return _readLength();
  }

  @Override
  public boolean isEOF() {
    return _context.length <= 1 && _readObject.length == 0;
  }

  @Override
  public int readInt8(String label) {
    return readProp<int>(label) ?? 0;
  }

  @Override
  public int readUint16(String label) {
    return readProp<int>(label) ?? 0;
  }

  @Override
  public Uint8List readUint8Array(int length, String label) {
    var ar = new Uint8List(length);
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
    return readProp<int>(label) ?? 0;
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
    return readProp<int>(label) ?? 0;
  }

  @Override
  public int readInt32(String label) {
    return readProp<int>(label) ?? 0;
  }

  @Override
  public int readVersion() {
    return readProp<int>("version") ?? 0;
  }

  @Override
  public String readString(String label) {
    return readProp<String>(label) ?? "";
  }

  @Override
  public boolean readBoolean(String label) {
    return readProp<boolean>(label) ?? false;
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
    dynamic array = readProp<dynamic>(label);
    _context.addFirst(array);
  }

  @Override
  public void closeArray() {
    _context.removeFirst();
  }

  @Override
  public void openObject(String label) {
    dynamic o = readProp<dynamic>(label);
    _context.addFirst(o);
  }

  @Override
  public void closeObject() {
    _context.removeFirst();
  }

  public int _readLength() {
    if (_context.first instanceof List) {
      return (_context.first as List).length;
    } else if (_context.first instanceof Map) {
      return (_context.first as Map).length;
    }
    return 0;
  }

  @Override
  public Uint8List readAsset() {
    String encodedAsset =
    readString("data"); // are we sure we need a label here?
    return final Base64Decoder().convert(encodedAsset, 22);
  }

  @Override
  public String containerType() { return "json"; }

  public ListQueue context() { return _context; }
}
