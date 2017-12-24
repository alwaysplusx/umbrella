package com.harmony.umbrella.fs.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.fs.StorageMetadata;

/**
 * @author wuxii@foxmail.com
 */
final class FileStorageMetadata implements StorageMetadata {

    private static final long serialVersionUID = 1L;

    String name;
    String path;
    long contentLength;
    String storageType;
    Map<String, Object> properties = new HashMap<String, Object>();

    FileStorageMetadata(String storageType) {
        this.storageType = storageType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

    @Override
    public String getStorageType() {
        return storageType;
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public String toString() {
        return "{name: " + name + ", path: " + path + ", type: " + storageType + "}";
    }

}
