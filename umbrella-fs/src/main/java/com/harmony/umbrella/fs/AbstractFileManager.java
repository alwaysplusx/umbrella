package com.harmony.umbrella.fs;

import java.io.File;
import java.io.InputStream;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractFileManager implements FileManager {

    private FileStorage fileStorage;
    private StorageType defaultStorageType = StorageType.SERVER;

    @Override
    public FileItem upload(File file) {
        return upload(file.getName(), file, defaultStorageType);
    }

    @Override
    public FileItem upload(String fileName, File file) {
        return upload(fileName, file, defaultStorageType);
    }

    @Override
    public FileItem upload(String fileName, File file, StorageType storageType) {
        return null;
    }

    @Override
    public FileItem upload(String fileName, InputStream in) {
        return null;
    }

    @Override
    public FileItem upload(String fileName, InputStream in, StorageType storageType) {
        return null;
    }

    @Override
    public FileItem download(Long fileId) {
        return null;
    }

    @Override
    public void delete(Long... fileId) {
    }

    @Override
    public void delete(FileItem... fileItem) {
    }

    public FileStorage getFileStorage() {
        return fileStorage;
    }

    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

}
