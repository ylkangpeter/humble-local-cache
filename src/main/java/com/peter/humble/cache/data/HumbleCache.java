package com.peter.humble.cache.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Not Thread-safe
 * Created by ylkang on 3/25/16.
 */
public class HumbleCache<T> {

    private Map<T, IndexWrapper> cache = new HashMap<T, IndexWrapper>();

    private Logger logger = LoggerFactory.getLogger(HumbleCache.class);

    private boolean slimMode = false;

    private Semaphore semaphore;

    private CacheDelegator cacheDelegator;

    /**
     * slim-mode reused existing index info and call 'get' to prepare for replacing every time.
     * So the performance is 1/2 of normal mode
     *
     * @param indexFile
     * @param dataFile
     * @param maxThreads
     * @param slimMode
     */
    public HumbleCache(String indexFile, String dataFile, int maxThreads, boolean slimMode) {
        this.cacheDelegator = new CacheDelegator(indexFile, dataFile);
        this.semaphore = new Semaphore(maxThreads);
        this.slimMode = slimMode;
        this.cacheDelegator.init();
    }

    public synchronized void save(T key, byte[] val) {
        if (val == null) {
            return;
        }
        try {
            IndexWrapper wrapper = cacheDelegator.save(key, val);
            cache.put(key, wrapper);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * value with insert timestamp&offset for debug etc
     *
     * @param key
     * @return
     */
    public ValueWrapper getVerbose(T key) {
        try {
            semaphore.tryAcquire(1000, TimeUnit.MILLISECONDS);
            try {
                IndexWrapper wrapper = cache.get(key);
                if (wrapper == null) {
                    return null;
                }
                return cacheDelegator.get(wrapper.getDataOffset(), wrapper.getBlock());
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            logger.warn("time out: " + e.getMessage(), e);
        }
        return null;
    }

    public byte[] get(T key) {
        ValueWrapper value = getVerbose(key);
        return value == null ? null : value.getBytes();
    }
}
