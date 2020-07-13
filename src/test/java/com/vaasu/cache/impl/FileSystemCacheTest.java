package com.vaasu.cache.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class FileSystemCacheTest {

    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";

    private FileSystemCache<Integer, String> fileSystemCache;

    @Before
    public void init() throws IOException {
        fileSystemCache = new FileSystemCache<>();
    }

    @After
    public void clearCache() throws IOException {
        fileSystemCache.clearCache();
    }

    @Test
    public void shouldPutGetAndRemoveObjectTest() throws IOException {
        fileSystemCache.putToCache(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.getFromCache(0));
        assertEquals(1, fileSystemCache.getCacheSize());

        fileSystemCache.removeFromCache(0);
        assertNull(fileSystemCache.getFromCache(0));
    }

    @Test
    public void shouldNotGetObjectFromCacheIfNotExistsTest() throws IOException {
        fileSystemCache.putToCache(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.getFromCache(0));
        assertNull(fileSystemCache.getFromCache(111));
    }

    @Test
    public void shouldNotRemoveObjectFromCacheIfNotExistsTest() throws IOException {
        fileSystemCache.putToCache(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.getFromCache(0));
        assertEquals(1, fileSystemCache.getCacheSize());

        fileSystemCache.removeFromCache(5);
        assertEquals(VALUE1, fileSystemCache.getFromCache(0));
    }

    @Test
    public void shouldGetCacheSizeTest() throws IOException {
        fileSystemCache.putToCache(0, VALUE1);
        assertEquals(1, fileSystemCache.getCacheSize());

        fileSystemCache.putToCache(1, VALUE2);
        assertEquals(2, fileSystemCache.getCacheSize());
    }

    @Test
    public void isObjectPresentTest() throws IOException {
        assertFalse(fileSystemCache.isObjectPresent(0));

        fileSystemCache.putToCache(0, VALUE1);
        assertTrue(fileSystemCache.isObjectPresent(0));
    }

    @Test
    public void isEmptyPlaceTest() throws IOException {
        fileSystemCache = new FileSystemCache<>(5);

        IntStream.range(0, 4).forEach(i -> {
            try {
                fileSystemCache.putToCache(i, "String " + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        assertTrue(fileSystemCache.hasEmptyPlace());
        fileSystemCache.putToCache(5, "String");
        assertFalse(fileSystemCache.hasEmptyPlace());
    }

    @Test
    public void shouldClearCacheTest() throws IOException {
        IntStream.range(0, 3).forEach(i -> {
            try {
                fileSystemCache.putToCache(i, "String " + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        assertEquals(3, fileSystemCache.getCacheSize());
        fileSystemCache.clearCache();
        assertEquals(0, fileSystemCache.getCacheSize());
    }
}
