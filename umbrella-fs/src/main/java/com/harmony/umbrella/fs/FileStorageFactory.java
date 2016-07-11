package com.harmony.umbrella.fs;

/**
 * @author wuxii@foxmail.com
 */
public interface FileStorageFactory {

    FileStorage getFileStorage(StorageType storageType);

}
