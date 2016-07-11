package com.huiju.module.fs.support;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.huiju.module.fs.FileStorage;
import com.huiju.module.fs.FileStorageFactory;
import com.huiju.module.fs.StorageType;

/**
 * @author wuxii@foxmail.com
 */
@Remote(FileStorageFactory.class)
@Stateless(mappedName = "FileStorageFactoryBean")
public class FileStorageFactoryBean implements FileStorageFactory {

    @Override
    public FileStorage getFileStorage(StorageType storageType) {
        return new ServerFileStorage();
    }

}
