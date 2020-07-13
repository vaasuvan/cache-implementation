package com.vaasu.cache.impl;

import com.vaasu.cache.Cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCache<K , V > implements Cache<K,V> {

    private final Map<K, V> objectsStorage;
    private final int capacity;

    public InMemoryCache(int capacity) {
        this.capacity = capacity;
        this.objectsStorage = new ConcurrentHashMap<>(capacity);
    }

    public void putToCache(K key, V value) {
        objectsStorage.put(key, value);
    }

    public V getFromCache(K key) {
        return objectsStorage.get(key);
    }

    public void removeFromCache(K key) {
        objectsStorage.remove(key);
    }

    public int getCacheSize() {
        return objectsStorage.size();
    }

    public boolean isObjectPresent(K key) {
        return objectsStorage.containsKey(key);
    }

    public boolean hasEmptyPlace() {
        return getCacheSize() < this.capacity;
    }

    public void clearCache() {
        objectsStorage.clear();
    }
}
