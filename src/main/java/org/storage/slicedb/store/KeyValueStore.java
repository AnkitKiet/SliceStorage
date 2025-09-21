package org.storage.slicedb.store;

public interface KeyValueStore {
    String get(String key);
    void put(String key, String value);
    void delete(String key);
}