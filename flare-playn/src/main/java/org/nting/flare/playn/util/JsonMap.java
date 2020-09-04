package org.nting.flare.playn.util;

import playn.core.Json;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

public class JsonMap extends AbstractMap {

    private final Set<Entry> entries;

    public JsonMap(Json.Object jsonObject) {
        this.entries = createEntries(jsonObject);
    }

    private Set<Entry> createEntries(Json.Object jsonObject) {
        Set<Entry> entries = new HashSet<>();

        for (String key : jsonObject.keys()) {
            if (jsonObject.isObject(key)) {
                entries.add(new SimpleEntry(key, new JsonMap(jsonObject.getObject(key))));
            } else if (jsonObject.isArray(key)) {
                entries.add(new SimpleEntry(key, new JsonList(jsonObject.getArray(key))));
            } else {
                entries.add(new SimpleEntry(key, jsonObject.get(key)));
            }
        }

        return entries;
    }

    @Override
    public Set<Entry> entrySet() {
        return entries;
    }
}
