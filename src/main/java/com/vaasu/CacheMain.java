package com.vaasu;

import com.vaasu.cache.Cache;
import com.vaasu.cache.impl.FileSystemCache;
import com.vaasu.cache.impl.InMemoryCache;
import com.vaasu.policies.LFUPolicy;
import com.vaasu.policies.LRUPolicy;
import com.vaasu.policies.Policy;
import com.vaasu.policies.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;

import static java.lang.String.format;

public class CacheMain<K , V > implements Cache<K,V> {

    static Logger log = LoggerFactory.getLogger(CacheMain.class);

    private final InMemoryCache<K, V> firstLevelCache;
    private final FileSystemCache<K, V> secondLevelCache;
    private final Policy<K> policy;

    public InMemoryCache<K, V> getFirstLevelCache() {
        return firstLevelCache;
    }

    public Policy<K> getPolicy() {
        return policy;
    }

    public CacheMain(final int inMemoryCacheSize, final int fileSystemCacheSize, PolicyType policyType) throws IOException {
        this.firstLevelCache = new InMemoryCache<>(inMemoryCacheSize);
        this.secondLevelCache = new FileSystemCache<K, V>(fileSystemCacheSize);
        this.policy = getPolicy(policyType);
    }

    private Policy<K> getPolicy(PolicyType policyType) {
        switch (policyType) {
            case LRU:
                return new LRUPolicy<K>();
            case LFU:
                return new LFUPolicy<>();
            default:
                return new LRUPolicy<K>();
        }
    }

    @Override
    public void putToCache(K key, V value) throws IOException {

        if (firstLevelCache.isObjectPresent(key) || firstLevelCache.hasEmptyPlace()) {
            log.debug(format("Put object with key %s to the 1st level", key));
            firstLevelCache.putToCache(key, value);
            if (secondLevelCache.isObjectPresent(key)) {
                secondLevelCache.removeFromCache(key);
            }
        } else if (secondLevelCache.isObjectPresent(key) || secondLevelCache.hasEmptyPlace()) {
            log.debug(format("Put object with key %s to the 2nd level", key));
            secondLevelCache.putToCache(key, value);
        } else {
            // Here we have full cache and have to replace some object with new one according to cache strategy.
            replaceObject(key, value);
        }

        if (!policy.isObjectPresent(key)) {
            log.debug(format("Put object with key %s to strategy", key));
            policy.putObject(key);
        }
    }

    private void replaceObject(K key, V value) throws IOException {
        K replacedKey = policy.getEvictedKey();
        if (firstLevelCache.isObjectPresent(replacedKey)) {
            log.debug(format("Replace object with key {} from 1st level", replacedKey));
            firstLevelCache.removeFromCache(replacedKey);
            firstLevelCache.putToCache(key, value);
        } else if (secondLevelCache.isObjectPresent(replacedKey)) {
            log.debug(format("Replace object with key %s from 2nd level", replacedKey));
            secondLevelCache.removeFromCache(replacedKey);
            secondLevelCache.putToCache(key, value);
        }
    }


    @Override
    public V getFromCache(K key) {
        if (firstLevelCache.isObjectPresent(key)) {
            policy.putObject(key);
            return firstLevelCache.getFromCache(key);
        } else if (secondLevelCache.isObjectPresent(key)) {
            policy.putObject(key);
            return secondLevelCache.getFromCache(key);
        }
        return null;
    }

    @Override
    public void removeFromCache(K key) {
        if (firstLevelCache.isObjectPresent(key)) {
            log.debug(format("Remove object with key %s from 1st level", key));
            firstLevelCache.removeFromCache(key);
        }
        if (secondLevelCache.isObjectPresent(key)) {
            log.debug(format("Remove object with key %s from 2nd level", key));
            secondLevelCache.removeFromCache(key);
        }
        policy.removeObject(key);
    }

    @Override
    public int getCacheSize() {
        return firstLevelCache.getCacheSize() + secondLevelCache.getCacheSize();
    }

    @Override
    public boolean isObjectPresent(K key) {
        return firstLevelCache.isObjectPresent(key) || secondLevelCache.isObjectPresent(key);    }

    @Override
    public boolean hasEmptyPlace() {
        return firstLevelCache.hasEmptyPlace() || secondLevelCache.hasEmptyPlace();
    }

    @Override
    public void clearCache() throws IOException {
        firstLevelCache.clearCache();
        secondLevelCache.clearCache();
        policy.clear();
    }
}
