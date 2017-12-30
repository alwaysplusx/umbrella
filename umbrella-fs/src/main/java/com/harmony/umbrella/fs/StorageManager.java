package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.Resource;

/**
 * @author wuxii@foxmail.com
 */
public interface StorageManager {

    String getStorageType();

    StorageMetadata putFile(File file) throws IOException;

    StorageMetadata putFile(File file, String name) throws IOException;

    StorageMetadata put(Resource resource) throws IOException;

    StorageMetadata put(Resource resource, String name) throws IOException;

    StorageMetadata put(Resource resource, Properties properties) throws IOException;

    Resource get(StorageMetadata sm) throws IOException;

}
