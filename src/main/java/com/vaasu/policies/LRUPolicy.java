package com.vaasu.policies;

public class LRUPolicy<K> extends Policy<K> {

    @Override
    public void putObject(K key) {
        getObjectsStorage().put(key,System.nanoTime());
    }
}

