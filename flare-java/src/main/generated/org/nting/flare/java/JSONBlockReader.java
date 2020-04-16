package org.nting.flare.java;

public class JSONBlockReader extends JSONReader {
  JSONBlockReader(Map object) : super(object);

  JSONBlockReader.fromObject(int type, Map object) : super(object) {
    blockType = type;
  }

  @Override
  public JSONBlockReader readNextBlock([Map<String, int> blockTypes]) {
    if (isEOF()) {
      return null;
    }

    var obj = <Object, Object>{};
    obj["container"] = _peek();
    var type = readBlockType(blockTypes);
    Object c = context.get(0);
    if (c instanceof Map) {
      c.remove(nextKey);
    } else if (c instanceof List) {
      c.removeAt(0);
    }

    return JSONBlockReader.fromObject(type, obj);
  }

  public int readBlockType(Map<String, int> blockTypes) {
    Object next = _peek();
    int bType;
    if (next instanceof Map) {
      Object c = context.get(0);
      if (c instanceof Map) {
        bType = blockTypes[nextKey];
      } else if (c instanceof List) {
        // Objects are serialized with "type" property.
        Object nType = next["type"];
        bType = blockTypes[nType];
      }
    } else if (next instanceof List) {
      // Arrays are serialized as "type": [Array].
      bType = blockTypes[nextKey];
    }
    return bType;
  }

  public Object _peek() {
    Object stream = context.get(0);
    Object next;
    if (stream instanceof Map) {
      next = stream[nextKey];
    } else if (stream instanceof List) {
      next = stream[0];
    }
    return next;
  }

  public Object nextKey() { return context.get(0).keys.get(0); }
}
