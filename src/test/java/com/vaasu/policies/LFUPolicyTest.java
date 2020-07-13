package com.vaasu.policies;

import com.vaasu.CacheMain;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LFUPolicyTest {

    private CacheMain<Integer, String> twoLevelCache;

    @After
    public void clearCache() throws IOException {
        twoLevelCache.clearCache();
    }

    @Test
    public void shouldMoveObjectFromCacheTest() throws IOException {
        twoLevelCache = new CacheMain<>(2, 2, PolicyType.LFU);

        twoLevelCache.putToCache(0, "String 0");
        twoLevelCache.getFromCache(0);
        twoLevelCache.getFromCache(0);
        twoLevelCache.putToCache(1, "String 1");
        twoLevelCache.getFromCache(1); // Least Frequently Used - will be removed
        twoLevelCache.putToCache(2, "String 2");
        twoLevelCache.getFromCache(2);
        twoLevelCache.getFromCache(2);
        twoLevelCache.putToCache(3, "String 3");
        twoLevelCache.getFromCache(3);
        twoLevelCache.getFromCache(3);

        assertTrue(twoLevelCache.isObjectPresent(0));
        assertTrue(twoLevelCache.isObjectPresent(1));
        assertTrue(twoLevelCache.isObjectPresent(2));
        assertTrue(twoLevelCache.isObjectPresent(3));

        twoLevelCache.putToCache(4, "String 4");
        twoLevelCache.getFromCache(4);
        twoLevelCache.getFromCache(4);

        assertTrue(twoLevelCache.isObjectPresent(0));
        assertFalse(twoLevelCache.isObjectPresent(1)); // Least Frequently Used - has been removed
        assertTrue(twoLevelCache.isObjectPresent(2));
        assertTrue(twoLevelCache.isObjectPresent(3));
        assertTrue(twoLevelCache.isObjectPresent(4));
    }

    @Test
    public void shouldNotRemoveObjectIfNotPresentTest() throws IOException {
        twoLevelCache = new CacheMain<>(1, 1, PolicyType.LFU);

        twoLevelCache.putToCache(0, "String 0");
        twoLevelCache.putToCache(1, "String 1");

        twoLevelCache.removeFromCache(2);

    }
}
