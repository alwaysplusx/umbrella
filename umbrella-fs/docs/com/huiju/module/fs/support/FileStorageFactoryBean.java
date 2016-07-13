package com.huiju.module.fs.support;

import com.huiju.module.fs.FileStorage;
import com.huiju.module.fs.FileStorageFactory;
import com.huiju.module.fs.StorageType;
import com.huiju.module.fs.util.FtpConfig;

/**
 * @author wuxii@foxmail.com
 */
public class FileStorageFactoryBean implements FileStorageFactory {

    // TODO load file store config and config it

    public FileStorageFactoryBean() {
    }

    @Override
    public FileStorage getFileStorage(StorageType storageType) {
        if (StorageType.SERVER.equals(storageType)) {
            return new FileSystemFileStorage();
        } else if (StorageType.FTP.equals(storageType)) {
            return new FtpFileStorage(new FtpConfig("192.168.0.170"));
        } else if (StorageType.AWS.equals(storageType)) {
            return new AwsS3FileStorage();
        }
        throw new IllegalStateException("unsupported type " + storageType);
    }

}
