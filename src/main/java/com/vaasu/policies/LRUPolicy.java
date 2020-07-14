package com.vaasu.policies;

public class LRUPolicy<K> extends Policy<K> {
    /**
     * method will be used to keep access-time of cache-object
     * @param key CacheObject key
     */
    @Override
    public void putObject(K key) {
        getObjectsStorage().put(key,System.nanoTime());
    }
}

