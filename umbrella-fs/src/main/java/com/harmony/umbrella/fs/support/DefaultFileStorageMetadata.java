package com.harmony.umbrella.fs.support;

import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.fs.FileStorageMetadata;
import com.harmony.umbrella.fs.StorageType;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultFileStorageMetadata implements FileStorageMetadata {

    private static final long serialVersionUID = 1L;

    private String fileName;
    private String original;
    private String destination;
    private Long contentLength;
    private StorageType storageType;
    protected Map<String, Object> properties = new HashMap<String, Object>();

    @Override
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
