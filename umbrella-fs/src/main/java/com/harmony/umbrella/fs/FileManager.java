package com.harmony.umbrella.fs;

import java.io.File;
import java.io.InputStream;

/**
 * @author wuxii@foxmail.com
 */
public interface FileManager {

    FileItem upload(File file);

    FileItem upload(String fileName, File file);

    FileItem upload(String fileName, File file, StorageType storageType);

    FileItem upload(String fileName, InputStream in);

    FileItem upload(String fileName, InputStream in, StorageType storageType);

    FileItem download(Long fileId);

    void delete(Long... fileId);

    void delete(FileItem... fileItem);

}
