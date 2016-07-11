package com.huiju.module.fs.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONException;
import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.fs.StorageType;
import com.huiju.module.util.FileUtils;
import com.huiju.module.util.IOUtils;
import com.huiju.module.util.TimeUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ServerFileStorage extends AbstractFileStorage {

    private static final long serialVersionUID = 1L;

    public static final String DIR = "/root/upload/";

    private String uploadDir = DIR;

    public ServerFileStorage() {
    }

    public ServerFileStorage(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public FileStorageMetadata putFile(InputStream in, Map<String, Object> properties) throws IOException {
        DefaultFileStorageMetadata storageMetadata = new DefaultFileStorageMetadata();
        
        storageMetadata.setStorageType(StorageType.SERVER);
        storageMetadata.setContentLength(Long.valueOf(in.available()));
        storageMetadata.setOriginal((String) properties.get(METADATA_ORIGINAL));
        storageMetadata.setFileName((String) properties.get(METADATA_FILE_NAMEL));

        File destinationFile = createDestinationFile((String) properties.get(METADATA_EXTENSION));
        storageMetadata.setDestination(destinationFile.getAbsolutePath());

        FileOutputStream fos = new FileOutputStream(destinationFile);
        IOUtils.copy(in, fos);
        fos.close();

        return storageMetadata;
    }

    @Override
    public File getFile(FileStorageMetadata metadata) throws IOException {
        File file = null;
        try {
            String path = metadata.getDestination();
            if (path == null || !(file = new File(path)).isFile()) {
                throw new IOException("file not fonud " + path);
            }
            return file;
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected File createDestinationFile(String extension) throws IOException {
        String path = uploadDir + TimeUtils.parseText(new Date(), "yyyyMMdd") + File.separator;

        if (!FileUtils.exists(path)) {
            FileUtils.createDirectory(path);
        } else if (FileUtils.isFile(path)) {
            throw new IOException("server path " + path + " not directory");
        }

        String filePath = path + UUID.randomUUID().toString().toUpperCase().replace("-", "") + (extension == null ? "" : extension);

        if (FileUtils.exists(filePath)) {
            throw new IOException("file already exist in server side");
        }

        return new File(filePath);
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.SERVER;
    }

}
