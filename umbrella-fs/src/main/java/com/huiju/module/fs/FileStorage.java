package com.huiju.module.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public interface FileStorage extends Serializable {

    final String METADATA_ORIGINAL = "original";

    final String METADATA_FILE_NAMEL = "fileName";

    final String METADATA_EXTENSION = "extension";

    final String METADATA_CONTENT_LENGTH = "contentLength";

    final String METADATA_DESTINATION = "destination";

    FileStorageMetadata putFile(File file) throws IOException;

    FileStorageMetadata putFile(File file, Map<String, Object> properties) throws IOException;

    FileStorageMetadata putFile(String fileName, InputStream in) throws IOException;

    FileStorageMetadata putFile(InputStream in, Map<String, Object> properties) throws IOException;

    File getFile(FileStorageMetadata metadata) throws IOException;

    InputStream getInputStream(FileStorageMetadata metadata) throws IOException;

    void deleteFile(FileStorageMetadata metadata) throws IOException;

    StorageType getStorageType();
    
}
