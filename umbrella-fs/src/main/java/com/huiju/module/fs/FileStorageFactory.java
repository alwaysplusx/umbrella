package com.huiju.module.fs;

/**
 * @author wuxii@foxmail.com
 */
public interface FileStorageFactory {

    FileStorage getFileStorage(StorageType storageType);

}
