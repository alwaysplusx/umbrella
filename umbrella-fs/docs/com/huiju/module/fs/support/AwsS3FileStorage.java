package com.huiju.module.fs.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.huiju.module.fs.FileMetadata;
import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.fs.StorageType;
import com.huiju.module.util.FileUtils;
import com.huiju.module.util.StringUtils;

/**
 * amazon s3 file storage
 * 
 * @author wuxii@foxmail.com
 */
public class AwsS3FileStorage extends AbstractFileStorage {

    public static final String DEFAULT_BUCKET_NAME = "huiju";

    private static final long serialVersionUID = 1L;

    private AmazonS3 s3 = new AmazonS3Client(new ProfileCredentialsProvider().getCredentials());

    private String bucketName = DEFAULT_BUCKET_NAME;

    @Override
    public FileStorageMetadata putFile(FileMetadata fileMetadata, Map<String, Object> properties) throws IOException {
        DefaultFileStorageMetadata fsm = new DefaultFileStorageMetadata(getStorageType());
        fsm.setFileName(fileMetadata.getFileName());
        fsm.setOriginal(fileMetadata.getAbsolutePath());

        InputStream is = fileMetadata.getInputStream();

        String bucketName = (String) properties.get("s3.bucketName");
        if (StringUtils.isBlank(bucketName)) {
            bucketName = this.bucketName;
        }

        ensureBucketExist(bucketName);

        String destinationFileName = createDestinationFileName(fsm.getFileName());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(is.available());
        HashMap<String, String> userMetadata = new HashMap<String, String>();
        userMetadata.put("huiju.fileName", fsm.getFileName());
        userMetadata.put("huiju.original", fsm.getOriginal());
        objectMetadata.setUserMetadata(userMetadata);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, destinationFileName, is, objectMetadata);
        PutObjectResult putObjectResult = s3.putObject(putObjectRequest);

        fsm.setContentLength(is.available());
        fsm.setDestination("s3:" + bucketName + ":" + destinationFileName);
        fsm.addProperty("s3.bucketName", bucketName);
        fsm.addProperty("s3.key", destinationFileName);
        fsm.addProperty("s3.expirationTime", putObjectResult.getExpirationTime());
        fsm.addProperty("s3.versionId", putObjectResult.getVersionId());
        fsm.addProperty("s3.eTag", putObjectResult.getETag());
        fsm.addProperty("s3.contentMd5", putObjectResult.getContentMd5());

        is.close();
        return fsm;
    }

    private void ensureBucketExist(String bucketName) {
        if (!s3.doesBucketExist(bucketName)) {
            s3.createBucket(bucketName);
        }
    }

    @Override
    public File getFile(FileStorageMetadata metadata) throws IOException {
        File destFile = FileUtils.createTempFile(true);
        getFile(metadata, destFile);
        return destFile;
    }

    public void getFile(FileStorageMetadata metadata, File destFile) {
        S3ObjectId objectId = createObjectId(metadata);
        GetObjectRequest getObjectRequest = new GetObjectRequest(objectId);
        s3.getObject(getObjectRequest, destFile);
    }

    @Override
    public void deleteFile(FileStorageMetadata metadata) throws IOException {
        S3ObjectId objectId = createObjectId(metadata);
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(objectId.getBucket(), objectId.getKey());
        s3.deleteObject(deleteObjectRequest);
    }

    private S3ObjectId createObjectId(FileStorageMetadata metadata) {
        Map<String, Object> properties = metadata.getProperties();
        String bucketName = (String) properties.get("s3.bucketName");
        String key = (String) properties.get("s3.key");
        String versionId = (String) properties.get("s3.versionId");
        if (StringUtils.isNotBlank(bucketName) && StringUtils.isNotBlank(key)) {
            String destination = metadata.getDestination();
            String[] destInfo = destination != null ? destination.split(":") : new String[0];
            if (StringUtils.isBlank(bucketName) && destInfo.length == 3) {
                bucketName = destInfo[1];
            }
            if (StringUtils.isBlank(key) && destInfo.length == 3) {
                key = destInfo[2];
            }
        }
        return new S3ObjectId(bucketName, key, versionId);
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.AWS;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public AmazonS3 getAmazonS3() {
        return s3;
    }

    public void setAmazonS3(AmazonS3 amazonS3) {
        this.s3 = amazonS3;
    }

}
