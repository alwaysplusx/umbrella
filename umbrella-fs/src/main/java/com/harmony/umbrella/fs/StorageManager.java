package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author wuxii@foxmail.com
 */
public interface StorageManager extends Serializable {

    String STORAGE_PATH = "umberlla-ss";

    StorageType getStorageType();

    StorageMetadata putFile(File file) throws IOException;

    StorageMetadata putFile(String name, File file) throws IOException;

    StorageMetadata put(String name, InputStream is) throws IOException;

}
