package com.harmony.umbrella.fs.support;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.harmony.umbrella.fs.StorageManager;
import com.harmony.umbrella.fs.StorageMetadata;
import com.harmony.umbrella.util.FileUtils;

/**
 * @author wuxii@foxmail.com
 */
abstract class AbstractStorageManager implements StorageManager {

    protected static final String UPLOAD_FILE_NAME = "fs.upload.name";

    protected final String storageType;

    public AbstractStorageManager(String storageType) {
        this.storageType = storageType;
    }

    // private void _init(ServiceProvider serviceProvider) {
    // init(serviceProvider);
    // }
    //
    // protected abstract void init(ServiceProvider serviceProvider);

    @Override
    public final String getStorageType() {
        return storageType;
    }

    /**
     * 根据源文件生成目标文件的文件名(保持扩展名不变)
     * 
     * @param file
     *            源文件
     * @return 新文件名
     */
    protected String generateFileName(File file) {
        String ext = FileUtils.getExtension(file);
        String name = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        return name + (ext == null ? "" : ext);
    }

    @Override
    public StorageMetadata putFile(File file) throws IOException {
        return putFile(file, null);
    }

    @Override
    public StorageMetadata putFile(File file, String name) throws IOException {
        return put(new FileSystemResource(file), name);
    }

    @Override
    public StorageMetadata put(Resource resource) throws IOException {
        return put(resource, (String) null);
    }

    @Override
    public StorageMetadata put(Resource resource, String name) throws IOException {
        return put(resource, name, new Properties());
    }

    @Override
    public StorageMetadata put(Resource resource, Properties properties) throws IOException {
        return put(resource, null, properties);
    }

    public abstract StorageMetadata put(Resource resource, String name, Properties properties) throws IOException;

}
