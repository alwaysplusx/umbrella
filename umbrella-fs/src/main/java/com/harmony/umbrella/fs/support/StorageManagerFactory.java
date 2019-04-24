package com.harmony.umbrella.fs.support;

import java.io.File;
import java.util.Map;

import com.aliyun.oss.OSSClient;
import com.amazonaws.services.s3.AmazonS3;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.OperatingSystemMetadata;
import com.harmony.umbrella.core.PropertyManager;
import com.harmony.umbrella.core.SimplePropertyManager;
import com.harmony.umbrella.fs.StorageManager;
import com.qiniu.util.Auth;

/**
 * @author wuxii@foxmail.com
 */
public class StorageManagerFactory {

    public static final String DEFAULT_ROOT_DIR;
    public static final String DEFAULT_BUCKET_NAME;

    static {
        OperatingSystemMetadata osm = OperatingSystemMetadata.INSTANCE;
        DEFAULT_ROOT_DIR = osm.userHome + File.separator + "upload";
        DEFAULT_BUCKET_NAME = osm.userName;
    }

    public static final String STORAGE_TYPE_OF_FILE_SYSTEM = FileSystemStorageManager.STORAGE_TYPE;
    public static final String STORAGE_TYPE_OF_QINIU = QiniuStorageManager.STORAGE_TYPE;
    public static final String STORAGE_TYPE_OF_FTP = FtpStorageManager.STORAGE_TYPE;
    public static final String STORAGE_TYPE_OF_AWS = AwsStorageManager.STORAGE_TYPE;
    public static final String STORAGE_TYPE_OF_ALIYUN = AliyunStroageManager.STORAGE_TYPE;

    public static final String SERVER_ROOT_DIR = "server.rootDir";
    public static final String SERVER_ROOT_DIR_VALUE = DEFAULT_ROOT_DIR;

    public static final String AWS_ACCESSKEY = "aws.accessKey";
    public static final String AWS_SECRETKEY = "aws.secretKey";
    public static final String AWS_BUCKETNAME = "aws.bucketName";
    public static final String AWS_BUCKETNAME_VALUE = DEFAULT_BUCKET_NAME;

    public static final String QINIU_ACCESSKEY = "qiniu.accessKey";
    public static final String QINIU_SECRETKEY = "qiniu.secretKey";
    public static final String QINIU_BUCKETNAME = "qiniu.bucketName";
    public static final String QINIU_BUCKETNAME_VALUE = DEFAULT_BUCKET_NAME;

    public static final String ALIYUN_ENDPOINT = "aliyun.endpoint";
    public static final String ALIYUN_ACCESSKEYID = "aliyun.accessKeyId";
    public static final String ALIYUN_SECRETACCESSKEY = "aliyun.secretAccessKey";
    public static final String ALIYUN_BUCKETNAME = "aliyun.bucketName";

    public static final String ALIYUN_BUCKETNAME_VALUE = DEFAULT_BUCKET_NAME;
    public static final String ALIYUN_ENDPOINT_VALUE = "http://oss.aliyuncs.com";

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
            String rootDir = propertyManager.getString(SERVER_ROOT_DIR, SERVER_ROOT_DIR_VALUE);
            return getFileSystemStorageManager(rootDir);
        }
        return null;
    }

    public FileSystemStorageManager getFileSystemStorageManager() {
        return getFileSystemStorageManager(DEFAULT_ROOT_DIR);
    }

    public FileSystemStorageManager getFileSystemStorageManager(String rootDir) {
        return new FileSystemStorageManager(rootDir);
    }

    public FtpStorageManager getFtpStorageManager() {
        return null;
    }

    public QiniuStorageManager getQiniuStorageManager(Auth auth, String bucketName) {
        return new QiniuStorageManager();
    }

    public AwsStorageManager getAwsStorageManager(AmazonS3 client, String bucketName) {
        return new AwsStorageManager(client, bucketName);
    }

    public AliyunStroageManager getAliyunStorageManager(OSSClient client, String bucketName) {
        return null;
    }

}
