package com.vaasu.policies;

import java.util.Map;
import java.util.TreeMap;

/**
 * This class will pick key of sorted value of  cache-object access-time/or  access-counter
 * @param <K>
 */

public abstract class Policy<K> {
    private final Map<K, Long> objectsStorage;
    private final TreeMap<K, Long> sortedObjectsStorage;

    Policy() {
        this.objectsStorage = new TreeMap<>();
        this.sortedObjectsStorage = new TreeMap<>(new ComparatorImpl<>(objectsStorage));
    }

    public Map<K, Long> getObjectsStorage() {
        return objectsStorage;
    }

    public abstract void putObject(K key);

    public void removeObject(K key) {
        if (isObjectPresent(key)) {
            objectsStorage.remove(key);
        }
    }

    public boolean isObjectPresent(K key) {
        return objectsStorage.containsKey(key);
    }

    public K getEvictedKey() {
        sortedObjectsStorage.putAll(objectsStorage);
        return sortedObjectsStorage.firstKey();
    }

    public void clear() {
        objectsStorage.clear();
    }
}
