package com.huiju.module.fs.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.huiju.module.fs.FileMetadata;
import com.huiju.module.fs.FileStorage;
import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.util.FileUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractFileStorage implements FileStorage {

    public static final String ROOT_DIR = "/root/upload/";

    private static final long serialVersionUID = 1L;

    protected String createDestinationFileName(String originalFileName) {
        String extension = FileUtils.getExtension(originalFileName);
        String fileName = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        return fileName + extension;
    }

    @Override
    public FileStorageMetadata putFile(FileMetadata fileMetadata) throws IOException {
        return putFile(fileMetadata, new HashMap<String, Object>());
    }

    @Override
    public FileStorageMetadata putFile(File file) throws IOException {
        return putFile(new FileMetadata(file), new HashMap<String, Object>());
    }

    public FileStorageMetadata putFile(String fileName, File file) throws IOException {
        return putFile(new FileMetadata(fileName, file), new HashMap<String, Object>());
    }

    public FileStorageMetadata putFile(String fileName, File file, Map<String, Object> properties) throws IOException {
        return putFile(new FileMetadata(fileName, file), properties);
    }

    /*public FileStorageMetadata putFile(String fileName, InputStream in, Map<String, Object> properties) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public FileStorageMetadata putFile(String fileName, InputStream in) throws IOException {
        return putFile(fileName, in, new HashMap<String, Object>());
    }*/

    public InputStream getInputStream(FileStorageMetadata metadata) throws IOException {
        return new FileInputStream(getFile(metadata));
    }

}
