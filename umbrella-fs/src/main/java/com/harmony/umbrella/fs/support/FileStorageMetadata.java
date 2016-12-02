package com.harmony.umbrella.fs.support;

import java.util.Properties;

import com.harmony.umbrella.fs.StorageMetadata;
import com.harmony.umbrella.fs.StorageType;

/**
 * @author wuxii@foxmail.com
 */
public final class FileStorageMetadata implements StorageMetadata {

    private static final long serialVersionUID = 1L;

    String name;
    String originalName;

    String path;
    String originalPath;

    long contentLength;
    StorageType storageType;

    Properties properties = new Properties();

    FileStorageMetadata(String originalName, String originalPath, StorageType storageType) {
        this.originalName = originalName;
        this.originalPath = originalPath;
        this.storageType = storageType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalName() {
        return originalName;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getOriginalPath() {
        return originalPath;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    @Override
    public Properties getProperties() {
        Properties copy = new Properties();
        copy.putAll(properties);
        return copy;
    }

}
