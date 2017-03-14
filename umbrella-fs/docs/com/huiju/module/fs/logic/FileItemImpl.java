package com.huiju.module.fs.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import com.huiju.module.fs.FileItem;
import com.huiju.module.fs.FileStorage;
import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.fs.StorageType;
import com.huiju.module.fs.entity.FileInfo;
import com.huiju.module.util.Converter;

/**
 * @author wuxii@foxmail.com
 */
public class FileItemImpl implements FileItem, Serializable {

    private static final long serialVersionUID = 1L;

    private FileInfo fileInfo;
    private FileStorage fileStorage;
    private FileStorageMetadata storageMetadata;
    private Converter<FileInfo, FileStorageMetadata> fileInfoToStorageMetadataConverter;

    public FileItemImpl() {
    }

    public FileItemImpl(FileInfo fileInfo, FileStorage fileStorage, FileStorageMetadata storageMetadata) {
        this.fileInfo = fileInfo;
        this.fileStorage = fileStorage;
        this.storageMetadata = storageMetadata;
    }

    public FileItemImpl(FileInfo fileInfo, FileStorage fileStorage, Converter<FileInfo, FileStorageMetadata> converter) {
        this.fileStorage = fileStorage;
        this.fileInfo = fileInfo;
        this.fileInfoToStorageMetadataConverter = converter;
    }

    @Override
    public Long getFileId() {
        return fileInfo.getFileInfoId();
    }

    @Override
    public String getFileName() {
        return fileInfo.getFileName();
    }

    @Override
    public StorageType getStorageType() {
        return fileInfo.getStorageType();
    }

    @Override
    public String getExtension() {
        return fileInfo.getFileExtension();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(getFile());
    }

    @Override
    public File getFile() throws IOException {
        return fileStorage.getFile(getMetadata());
    }

    public FileStorage getFileStorage() {
        return fileStorage;
    }

    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public FileStorageMetadata getStorageMetadata() {
        return storageMetadata;
    }

    public void setStorageMetadata(FileStorageMetadata storageMetadata) {
        this.storageMetadata = storageMetadata;
    }

    public Converter<FileInfo, FileStorageMetadata> getConverter() {
        return fileInfoToStorageMetadataConverter;
    }

    public void setConverter(Converter<FileInfo, FileStorageMetadata> fileInfoToStorageMetadata) {
        this.fileInfoToStorageMetadataConverter = fileInfoToStorageMetadata;
    }

    private FileStorageMetadata getMetadata() {
        if (storageMetadata == null && fileInfoToStorageMetadataConverter != null) {
            storageMetadata = fileInfoToStorageMetadataConverter.convert(fileInfo);
        }
        return storageMetadata;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

}
