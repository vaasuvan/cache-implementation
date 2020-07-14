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

/**
 * This class will be used for archive  both caching mechanism InMemory and FileSystem
 * and Eviction Policy
 * @param <K>
 * @param <V>
 */
public class CacheMain<K extends Serializable, V extends Serializable > implements Cache<K,V> {

    static Logger log = LoggerFactory.getLogger(CacheMain.class);

    private final InMemoryCache<K, V> firstLevelCache;
    private final FileSystemCache<K, V> secondLevelCache;
    private final Policy<K> policy;

    public CacheMain(final int inMemoryCacheSize, final int fileSystemCacheSize, PolicyType policyType) throws IOException {
        this.firstLevelCache = new InMemoryCache<>(inMemoryCacheSize);
        this.secondLevelCache = new FileSystemCache<>(fileSystemCacheSize);
        this.policy = getPolicy(policyType);
    }

    private Policy<K> getPolicy(PolicyType policyType) {
        switch (policyType) {
            case LFU:
                return new LFUPolicy<>();
            default:
                return new LRUPolicy<>();
        }
    }

    @Override
    public void putToCache(K key, V value) throws IOException {

        if (firstLevelCache.isObjectPresent(key) || firstLevelCache.hasEmptyPlace()) {
            log.debug("====PUT OBJECT WITH KEY {} TO THE 1ST LEVEL===", key);
            firstLevelCache.putToCache(key, value);
            if (secondLevelCache.isObjectPresent(key)) {
                secondLevelCache.removeFromCache(key);
            }
        } else if (secondLevelCache.isObjectPresent(key) || secondLevelCache.hasEmptyPlace()) {
            log.debug("====PUT OBJECT WITH KEY {} TO THE 2ND LEVEL====", key);
            secondLevelCache.putToCache(key, value);
        } else {
            replaceObject(key, value);
        }

        if (!policy.isObjectPresent(key)) {
            log.debug("=======PUT OBJECT WITH KEY {} TO STRATEGY=====", key);
            policy.putObject(key);
        }
    }

    private void replaceObject(K key, V value) throws IOException {
        K replacedKey = policy.getEvictedKey();
        if (firstLevelCache.isObjectPresent(replacedKey)) {
            log.debug("====REPLACE OBJECT WITH KEY {} FROM 1ST LEVEL======", replacedKey);
            firstLevelCache.removeFromCache(replacedKey);
            firstLevelCache.putToCache(key, value);
        } else if (secondLevelCache.isObjectPresent(replacedKey)) {
            log.debug("====REPLACE OBJECT WITH KEY {}FROM 2ND LEVEL======", replacedKey);
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
            log.debug("====REMOVE OBJECT WITH KEY {} FROM 1ST LEVEL====", key);
            firstLevelCache.removeFromCache(key);
        }
        if (secondLevelCache.isObjectPresent(key)) {
            log.debug("=====REMOVE OBJECT WITH KEY {} FROM 2ND LEVEL======", key);
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
