package com.harmony.umbrella.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * @author wuxii@foxmail.com
 */
public class CachePool {

    private static final String DEFAULT_CONFIG_LOCATION = "ehcache.xml";
    private final CacheManager cacheManager;
    private final Cache cache;

    public CachePool(String cacheName) throws CacheException {
        this(DEFAULT_CONFIG_LOCATION, cacheName);
    }

    public CachePool(String configFileName, String cacheName) throws CacheException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(configFileName));
            this.cacheManager = create(fis);
            this.cache = cacheManager.getCache(cacheName);
        } catch (FileNotFoundException e) {
            throw new CacheException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public CachePool(InputStream inputStream, String cacheName) {
        this.cacheManager = create(inputStream);
        this.cache = cacheManager.getCache(cacheName);
    }

    public boolean put(Object key, Object value) {
        return false;
    }

    public boolean put(Element element) {
        return false;
    }

    public <T> T get(Object key) {
        return null;
    }

    public boolean removeAll() {
        return false;
    }

    public boolean remove(Object key) {
        return false;
    }

    public List<?> getKeys() {
        return null;
    }

    public String getCacheName() {
        return cache.getName();
    }

    public static CacheManager create(InputStream inputStream) {
        return CacheManager.create(inputStream);
    }

    public static CacheManager create(String configLocation) {
        return CacheManager.create(configLocation);
    }

}
