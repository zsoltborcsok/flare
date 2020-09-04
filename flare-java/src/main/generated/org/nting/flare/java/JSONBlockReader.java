package org.nting.flare.java;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONBlockReader extends JSONReader {

    public int blockType;

    public JSONBlockReader(Map object) {
        super(object);
        blockType = 0;
    }

    public JSONBlockReader(int type, Map object) {
        super(object);
        blockType = type;
    }

    @Override
    public int blockType() {
        return blockType;
    }

    @Override
    public JSONBlockReader readNextBlock(Map<String, Integer> blockTypes) {
        if (isEOF()) {
            return null;
        }

        Map obj = new HashMap();
        obj.put("container", _peek());
        int type = readBlockType(blockTypes);
        Object c = context().get(0);
        if (c instanceof AbstractMap) {
            ((Map) c).remove(nextKey());
        } else if (c instanceof AbstractList) {
            ((List) c).remove(0);
        }

        return new JSONBlockReader(type, obj);
    }

    public int readBlockType(Map<String, Integer> blockTypes) {
        Object next = _peek();
        int bType = 0;
        if (next instanceof AbstractMap) {
            Object c = context().get(0);
            if (c instanceof AbstractMap) {
                bType = blockTypes.get(nextKey());
            } else if (c instanceof AbstractList) {
                // Objects are serialized with "type" property.
                Object nType = ((Map) next).get("type");
                bType = blockTypes.get(nType);
            }
        } else if (next instanceof AbstractList) {
            // Arrays are serialized as "type": [Array].
            bType = blockTypes.get(nextKey());
        }
        return bType;
    }

    public Object _peek() {
        Object stream = context().get(0);
        Object next = null;
        if (stream instanceof AbstractMap) {
            next = ((Map) stream).get(nextKey());
        } else if (stream instanceof AbstractList) {
            next = ((List) stream).get(0);
        }
        return next;
    }

    public Object nextKey() {
        return ((Map) context().get(0)).keySet().iterator().next(); // ??? "nextKey => context.first.keys.first"
    }
}
