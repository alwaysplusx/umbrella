package com.huiju.module.fs.logic;

import java.io.File;
import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

import com.huiju.module.data.eao.GenericEao;
import com.huiju.module.data.logic.GenericLogicImpl;
import com.huiju.module.fs.FileItem;
import com.huiju.module.fs.FileMetadata;
import com.huiju.module.fs.FileStorage;
import com.huiju.module.fs.FileStorageFactory;
import com.huiju.module.fs.FileStorageMetadata;
import com.huiju.module.fs.StorageType;
import com.huiju.module.fs.eao.FileInfoEaoLocal;
import com.huiju.module.fs.entity.FileInfo;
import com.huiju.module.fs.support.FileStorageFactoryBean;
import com.huiju.module.util.Converter;

// TODO 未完成

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "FileInfoBean")
public class FileInfoBean extends GenericLogicImpl<FileInfo, Long> implements FileInfoLocal, FileInfoRemote {

    @EJB
    private FileInfoEaoLocal fileInfoEao;

    private FileStorageFactory fileStorageFactory = new FileStorageFactoryBean();

    private Converter<FileStorageMetadata, FileInfo> storageMetadataToFileInfoConverter = new ConverterToFileInfo();

    private StorageType storageType = StorageType.SERVER;

    @Override
    protected GenericEao<FileInfo, Long> getGenericEao() {
        return fileInfoEao;
    }

    @Override
    public FileItem upload(File file) {
        return upload(file.getName(), file, storageType);
    }

    @Override
    public FileItem upload(File file, StorageType storageType) {
        return upload(file.getName(), file, storageType);
    }

    @Override
    public FileItem upload(String fileName, File file) {
        return upload(fileName, file, storageType);
    }

    @Override
    public FileItem upload(String fileName, File file, StorageType storageType) {
        FileStorage fileStorage = fileStorageFactory.getFileStorage(storageType);
        try {
            FileStorageMetadata storageMetadata = fileStorage.putFile(new FileMetadata(fileName, file));
            FileInfo fileInfo = storageMetadataToFileInfoConverter.convert(storageMetadata);
            fileInfo = persist(fileInfo);
            return new FileItemImpl(fileInfo, fileStorage, storageMetadata);
        } catch (IOException e) {
            throw new EJBException(e);
        }
    }

    @Override
    public FileItem download(Long fileId) {
        FileInfo fileInfo = find(fileId);
        return fileInfo != null ? convert(fileInfo) : null;
    }

    @Override
    public void delete(Long... fileId) {
    }

    @Override
    public void delete(FileItem... fileItem) {
    }

    @Override
    public FileItem convert(FileInfo fileInfo) {
        FileStorage fileStorage = fileStorageFactory.getFileStorage(fileInfo.getStorageType());
        return new FileItemImpl(fileInfo, fileStorage, new ConverterToFileStorageMetadata());
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    @Override
    public FileItem upload(File file, Long groupId) {
        return null;
    }

    @Override
    public FileItem upload(String fileName, File file, Long groupId) {
        return null;
    }

    @Override
    public void group(Long... items) {
    }

    @Override
    public void group(FileItem... items) {
    }

}
