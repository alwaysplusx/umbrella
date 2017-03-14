package com.huiju.module.fs;

/**
 * 文件存储的工厂类
 * 
 * @author wuxii@foxmail.com
 */
public interface FileStorageFactory {

    /**
     * 根据不同的文件存储类型创建文件存储实例
     * 
     * @param storageType
     *            文件存储类型
     * @return 文件存储管理类
     */
    FileStorage getFileStorage(StorageType storageType);

}
