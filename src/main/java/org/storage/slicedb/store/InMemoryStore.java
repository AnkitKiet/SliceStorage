package org.storage.slicedb.store;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore implements KeyValueStore {
    private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void delete(String key) {
        map.remove(key);
    }
}
