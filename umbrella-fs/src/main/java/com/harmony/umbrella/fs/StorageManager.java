package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import com.harmony.umbrella.io.FileSystemResource;
import com.harmony.umbrella.io.Resource;

/**
 * @author wuxii@foxmail.com
 */
public interface StorageManager extends Serializable {

    String STORAGE_PATH = "umberlla-ss";

    StorageType getStorageType();

    default StorageMetadata putFile(File file) throws IOException {
        return putFile(file.getName(), file);
    }

    default StorageMetadata putFile(String name, File file) throws IOException {
        return put(name, new FileSystemResource(file));
    }

    default StorageMetadata put(Resource resource) throws IOException {
        return put(resource.getFilename(), resource);
    }

    default StorageMetadata put(String name, Resource resource) throws IOException {
        return put(name, resource, new Properties());
    }

    StorageMetadata put(String name, Resource resource, Properties properties) throws IOException;

}
