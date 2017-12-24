package com.harmony.umbrella.fs.support;

import java.util.Map;

import com.amazonaws.services.s3.AmazonS3;
import com.harmony.umbrella.core.PropertyManager;
import com.harmony.umbrella.core.SimplePropertyManager;
import com.harmony.umbrella.fs.StorageManager;
import com.qiniu.util.Auth;

/**
 * @author wuxii@foxmail.com
 */
public class StorageManagerFactory {

    public static final String STORAGE_TYPE_OF_FILE_SYSTEM = FileSystemStorageManager.STORAGE_TYPE;
    public static final String STORAGE_TYPE_OF_QINIU = QiniuStorageManager.STORAGE_TYPE;
    public static final String STORAGE_TYPE_OF_FTP = FtpStorageManager.STORAGE_TYPE;
    public static final String STORAGE_TYPE_OF_AWS = AwsStorageManager.STORAGE_TYPE;
    public static final String STORAGE_TYPE_OF_ALIYUN = AliyunStroageManager.STORAGE_TYPE;

    PropertyManager propertyManager;

    public StorageManagerFactory(Map properties) {
        this(new SimplePropertyManager(properties));
    }

    public StorageManagerFactory(PropertyManager propertyManager) {
        // FIXME 运用配置来构建对应的storage
        this.propertyManager = propertyManager;
    }

    public StorageManager getStorageManager(String storageType) {
        if (STORAGE_TYPE_OF_FILE_SYSTEM.equals(storageType)) {
            return getFileSystemStorageManager();
        }
        return null;
    }

    public FileSystemStorageManager getFileSystemStorageManager() {
        return new FileSystemStorageManager();
    }

    public FtpStorageManager getFtpStorageManager() {
        return null;
    }

    public FileSystemStorageManager getFileSystemStorageManager(String rootDir) {
        return new FileSystemStorageManager(rootDir);
    }

    public QiniuStorageManager getQiniuStorageManager(Auth auth, String bucketName) {
        return new QiniuStorageManager();
    }

    public AwsStorageManager getAwsStorageManager(AmazonS3 client, String bucketName) {
        return new AwsStorageManager(client, bucketName);
    }

}
