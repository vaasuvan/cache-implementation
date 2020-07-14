package com.vaasu.policies;

import com.vaasu.CacheMain;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LRUPolicyTest {

    private CacheMain<Integer, String> cacheMain;

    @After
    public void clearCache() throws IOException {
        cacheMain.clearCache();
    }

    @Test
    public void shouldMoveObjectFromCacheTest() throws IOException {
        cacheMain = new CacheMain<>(2, 2, PolicyType.LRU);

        // i=0 - Least Recently Used - will be removed
        IntStream.range(0, 4).forEach(i -> {
            try {
                cacheMain.putToCache(i, "String " + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assertTrue(cacheMain.isObjectPresent(i));
            cacheMain.getFromCache(i);
        });

        cacheMain.putToCache(4, "String 4");

        assertFalse(cacheMain.isObjectPresent(0)); //Least Recently Used - has been removed
        assertTrue(cacheMain.isObjectPresent(1));
        assertTrue(cacheMain.isObjectPresent(2));
        assertTrue(cacheMain.isObjectPresent(3));
        assertTrue(cacheMain.isObjectPresent(4));
    }
}
