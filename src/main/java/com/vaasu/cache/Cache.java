package com.vaasu.cache;

import java.io.IOException;

public interface Cache<K,V> {

    void putToCache(K key, V value) throws IOException;

    V getFromCache(K key);

    void removeFromCache(K key);

    int getCacheSize();

    boolean isObjectPresent(K key);

    boolean hasEmptyPlace();

    void clearCache() throws IOException;
}
