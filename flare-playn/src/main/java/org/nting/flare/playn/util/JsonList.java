package org.nting.flare.playn.util;

import playn.core.Json;

import java.util.AbstractList;

public class JsonList extends AbstractList {

    private final Json.Array jsonArray;

    public JsonList(Json.Array jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public Object get(int index) {
        if (jsonArray.isObject(index)) {
            return new JsonMap(jsonArray.getObject(index));
        } else if (jsonArray.isArray(index)) {
            return new JsonList(jsonArray.getArray(index));
        } else {
            return jsonArray.get(index);
        }
    }

    @Override
    public int size() {
        return jsonArray.length();
    }

    @Override
    public Object remove(int index) {
        Object element = get(index);
        jsonArray.remove(index);
        return element;
    }
}
