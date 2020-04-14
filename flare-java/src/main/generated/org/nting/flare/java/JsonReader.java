package org.nting.flare.java;

public abstract class JSONReader implements StreamReader {
  @override
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
    if (head is Map) {
      dynamic prop = head[label];
      head.remove(label);
      if (prop is T) {
        return prop;
      } else {
        return null;
      }
    } else if (head is List) {
      dynamic prop = head.removeAt(0);
      if (prop is T) {
        return prop;
      } else {
        return null;
      }
    }
    return null;
  }

  @override
  public double readFloat32(String label) {
    num f = readProp<num>(label);
    return f?.toDouble() ?? 0.0;
  }

  // Reads the array into ar
  @override
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
      ar[i] = ar.first is double ? val.toDouble() : val.toInt();
    }
  }

  @override
  public double readFloat64(String label) {
    num f = readProp<num>(label);
    return f?.toDouble() ?? 0;
  }

  @override
  public int readUint8(String label) {
    return readProp(label) ?? 0;
  }

  @override
  public int readUint8Length() {
    return _readLength();
  }

  @override
  public bool isEOF() {
    return _context.length <= 1 && _readObject.length == 0;
  }

  @override
  public int readInt8(String label) {
    return readProp<int>(label) ?? 0;
  }

  @override
  public int readUint16(String label) {
    return readProp<int>(label) ?? 0;
  }

  @override
  public Uint8List readUint8Array(int length, String label) {
    var ar = new Uint8List(length);
    _readArray(ar, label);
    return ar;
  }

  @override
  public Uint16List readUint16Array(int length, String label) {
    var ar = new Uint16List(length);
    _readArray(ar, label);
    return ar;
  }

  @override
  public int readInt16(String label) {
    return readProp<int>(label) ?? 0;
  }

  @override
  public int readUint16Length() {
    return _readLength();
  }

  @override
  public int readUint32Length() {
    return _readLength();
  }

  @override
  public int readUint32(String label) {
    return readProp<int>(label) ?? 0;
  }

  @override
  public int readInt32(String label) {
    return readProp<int>(label) ?? 0;
  }

  @override
  public int readVersion() {
    return readProp<int>("version") ?? 0;
  }

  @override
  public String readString(String label) {
    return readProp<String>(label) ?? "";
  }

  @override
  public bool readBool(String label) {
    return readProp<bool>(label) ?? false;
  }

  // @hasOffset flag is needed for older (up until version 14) files.
  // Since the JSON Reader has been added in version 15, the field
  // here is optional.
  @override
  public int readId(String label) {
    var val = readProp<num>(label);
    return val != null ? val.toInt() + 1 : 0;
  }

  @override
  public void openArray(String label) {
    dynamic array = readProp<dynamic>(label);
    _context.addFirst(array);
  }

  @override
  public void closeArray() {
    _context.removeFirst();
  }

  @override
  public void openObject(String label) {
    dynamic o = readProp<dynamic>(label);
    _context.addFirst(o);
  }

  @override
  public void closeObject() {
    _context.removeFirst();
  }

  public int _readLength() {
    if (_context.first is List) {
      return (_context.first as List).length;
    } else if (_context.first is Map) {
      return (_context.first as Map).length;
    }
    return 0;
  }

  @override
  public Uint8List readAsset() {
    String encodedAsset =
    readString("data"); // are we sure we need a label here?
    return const Base64Decoder().convert(encodedAsset, 22);
  }

  @override
  String get containerType => "json";

  ListQueue get context => _context;
}
