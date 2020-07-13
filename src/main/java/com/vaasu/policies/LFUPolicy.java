package com.vaasu.policies;

public class LFUPolicy<K> extends Policy<K> {

    @Override
    public void putObject(K key) {
        long frequency = 1;
        if (getObjectsStorage().containsKey(key)) {
            frequency = getObjectsStorage().get(key) + 1;
        }
        getObjectsStorage().put(key, frequency);
    }
}
