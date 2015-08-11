/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
