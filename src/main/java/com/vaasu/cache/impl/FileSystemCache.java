package com.vaasu.cache.impl;

import com.vaasu.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

public class FileSystemCache<K , V > implements Cache<K,V> {
    Logger log = LoggerFactory.getLogger(FileSystemCache.class);
    private final Map<K, String> objectsStorage;
    private final Path tempDir;
    private int capacity;

    public FileSystemCache() throws IOException {
        this.tempDir = Files.createTempDirectory("cache");
        this.tempDir.toFile().deleteOnExit();
        this.objectsStorage = new ConcurrentHashMap<>();
    }

    public FileSystemCache(int capacity) throws IOException {
        this.tempDir = Files.createTempDirectory("cache");
        this.tempDir.toFile().deleteOnExit();
        this.capacity = capacity;
        this.objectsStorage = new ConcurrentHashMap<>(capacity);
    }

    @Override
    public void putToCache(K key, V value) throws IOException {
        File tmpFile = Files.createTempFile(tempDir, "", "").toFile();

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tmpFile))) {
            outputStream.writeObject(value);
            outputStream.flush();
            objectsStorage.put(key, tmpFile.getName());
        } catch (IOException e) {
            log.error("#########CAN'T WRITE AN OBJECT TO A FILE #########" + tmpFile.getName() + ": " + e.getMessage());
        }
    }

    @Override
    public V getFromCache(K key) {
        if (isObjectPresent(key)) {
            String fileName = objectsStorage.get(key);
            try (FileInputStream fileInputStream = new FileInputStream(new File(tempDir + File.separator + fileName));
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return (V) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                log.error(format("##########CAN'T READ A FILE.########## %s: %s", fileName, e.getMessage()));
            }
        }
        log.debug(format("====OBJECT WITH KEY '%s' DOES NOT EXIST=======", key));
        return null;
    }

    @Override
    public void removeFromCache(K key) {
        String fileName = objectsStorage.get(key);
        File deletedFile = new File(tempDir + File.separator + fileName);
        if (deletedFile.delete()) {
            log.debug(format("======CACHE FILE '%s' HAS BEEN DELETED======", fileName));
        } else {
            log.debug(format("######CAN'T DELETE A FILE####### %s", fileName));
        }
        objectsStorage.remove(key);
    }

    @Override
    public int getCacheSize() {
        return objectsStorage.size();
    }

    @Override
    public boolean isObjectPresent(K key) {
        return objectsStorage.containsKey(key);
    }

    @Override
    public boolean hasEmptyPlace() {
        return getCacheSize() < this.capacity;
    }

    @Override
    public void clearCache() throws IOException {
        Files.walk(tempDir)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(file -> {
                    if (file.delete()) {
                        log.debug("=======CACHE FILE '{}' HAS BEEN DELETED======", file);
                    } else {
                        log.error("#########CAN'T DELETE A FILE #########{}", file);
                    }
                });
        objectsStorage.clear();
    }
}
