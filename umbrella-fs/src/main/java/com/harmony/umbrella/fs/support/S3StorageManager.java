package com.harmony.umbrella.fs.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.harmony.umbrella.fs.StorageMetadata;
import com.harmony.umbrella.fs.StorageType;
import com.harmony.umbrella.io.Resource;

/**
 * @author wuxii@foxmail.com
 */
public class S3StorageManager extends AbstractStorageManager {

    private static final long serialVersionUID = 1L;

    private AmazonS3 s3;

    private String bucketName;

    public S3StorageManager(AmazonS3 s3) {
        this(s3, STORAGE_PATH);
    }

    public S3StorageManager(AmazonS3 s3, String bucketName) {
        super(StorageType.AWS);
        this.s3 = s3;
        this.bucketName = bucketName;
    }

    @Override
    public StorageMetadata put(String name, Resource resource, Properties properties) throws IOException {
        return put(name, resource, bucketName, properties, null);
    }

    public StorageMetadata put(String name, Resource resource, String bucketName, Properties properties, ObjectMetadataConfig config) throws IOException {
        FileStorageMetadata fsm = new FileStorageMetadata(name, getResourcePath(resource), storageType);
        if (!s3.doesBucketExist(bucketName)) {
            s3.createBucket(bucketName);
        }

        InputStream is = resource.getInputStream();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        if (config != null) {
            config.config(objectMetadata);
        }
        // XXX 可扩展点, 服务器中的存储名称生成
        objectMetadata.addUserMetadata("originalName", resource.getFilename());
        PutObjectRequest request = new PutObjectRequest(bucketName, UUID.randomUUID().toString(), is, objectMetadata);
        PutObjectResult result = s3.putObject(request);

        fsm.properties.put("s3.bucketName", bucketName);
        fsm.properties.put("s3.versionId", result.getVersionId());
        // fsm.addProperty("s3.key", destinationFileName);
        // fsm.addProperty("s3.expirationTime", putObjectResult.getExpirationTime());
        // fsm.addProperty("s3.eTag", putObjectResult.getETag());
        // fsm.addProperty("s3.contentMd5", putObjectResult.getContentMd5());
        return fsm;
    }

    protected ObjectMetadata createObjectMetadata(String name, ObjectMetadataConfig config) {
        return null;
    }

    public interface ObjectMetadataConfig {

        void config(ObjectMetadata metadata);

    }

}
