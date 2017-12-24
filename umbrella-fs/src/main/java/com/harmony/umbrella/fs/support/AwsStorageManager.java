package com.harmony.umbrella.fs.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.fs.StorageMetadata;
import com.harmony.umbrella.util.PropertiesUtils;

/**
 * @author wuxii@foxmail.com
 */
public class AwsStorageManager extends AbstractStorageManager {

    public static final String STORAGE_TYPE = "aws";

    public static final String DEFAULT_BUCKET_NAME;

    static {
        DEFAULT_BUCKET_NAME = ApplicationMetadata.getOperatingSystemMetadata().userName.replace(" ", "_");
    }

    private AmazonS3 client;

    private String bucketName;

    public AwsStorageManager(AmazonS3 s3) {
        this(s3, DEFAULT_BUCKET_NAME);
    }

    public AwsStorageManager(AmazonS3 s3, String bucketName) {
        super(STORAGE_TYPE);
        this.client = s3;
        this.bucketName = bucketName;
    }

    @Override
    public StorageMetadata put(Resource resource, String name, Properties properties) throws IOException {
        return put(resource, name, properties, null);
    }

    public StorageMetadata put(Resource resource, String name, Properties properties, PutObjectRequestConfigure configure) throws IOException {
        if (properties == null) {
            properties = new Properties();
        }
        FileStorageMetadata fsm = new FileStorageMetadata(storageType);

        if (!client.doesBucketExist(bucketName)) {
            client.createBucket(bucketName);
        }

        String key = generateFileName(resource.getFile());

        try (InputStream is = resource.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            PutObjectRequest request = new PutObjectRequest(bucketName, key, is, objectMetadata);

            if (configure != null) {
                configure.config(request);
                // in case
                request.setBucketName(bucketName);
                request.setKey(key);
                request.setFile(null);
                request.setInputStream(is);
            }

            PutObjectResult result = client.putObject(request);

            PropertiesUtils.apply(fsm.properties, properties);
            fsm.properties.put("s3.bucketName", bucketName);
            fsm.properties.put("s3.versionId", result.getVersionId());
            fsm.properties.put("s3.key", key);
            fsm.properties.put("s3.expirationTime", result.getExpirationTime());
            fsm.properties.put("s3.eTag", result.getETag());
            fsm.properties.put("s3.contentMd5", result.getContentMd5());

            return fsm;
        }
    }

    @Override
    public Resource get(StorageMetadata sm) throws IOException {
        throw new UnsupportedOperationException();
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setAwsS3Client(AmazonS3 client) {
        this.client = client;
    }

    public interface PutObjectRequestConfigure {

        void config(PutObjectRequest request);

    }

}
