package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public interface FileStorage extends Serializable {

    StorageType getStorageType();

    FileStorageMetadata putFile(File file) throws IOException;

    FileStorageMetadata putFile(String fileName, File file) throws IOException;

    FileStorageMetadata putFile(String fileName, InputStream is) throws IOException;

    FileStorageMetadata putFile(FileMetadata fileMetadata) throws IOException;

    FileStorageMetadata putFile(FileMetadata fileMetadata, Map<String, Object> properties) throws IOException;

    File getFile(FileStorageMetadata metadata) throws IOException;

    InputStream getInputStream(FileStorageMetadata metadata) throws IOException;

    void deleteFile(FileStorageMetadata metadata) throws IOException;

}
