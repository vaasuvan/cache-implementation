package com.vaasu.policies;

/**
 * This class will be used to manage Least Frequently Used CacheObjects
 * @param <K>
 */
public class LFUPolicy<K> extends Policy<K> {

    /**
     * method will be used access counter of cache-object
     * @param key CacheObject key
     */
    @Override
    public void putObject(K key) {
        long frequency = 1;
        if (getObjectsStorage().containsKey(key)) {
            frequency = getObjectsStorage().get(key) + 1;
        }
        getObjectsStorage().put(key, frequency);
    }
}
