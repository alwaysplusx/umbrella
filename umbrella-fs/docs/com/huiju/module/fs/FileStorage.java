package com.huiju.module.fs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * 文件存储的接口，不同存储介质具有不同的实现
 * 
 * @author wuxii@foxmail.com
 */
public interface FileStorage extends Serializable {

    FileStorageMetadata putFile(File file) throws IOException;

    FileStorageMetadata putFile(FileMetadata fileMetadata) throws IOException;

    FileStorageMetadata putFile(FileMetadata fileMetadata, Map<String, Object> properties) throws IOException;

    /**
     * 根据存储凭证下载对应的文件
     * 
     * @param metadata
     *            存储凭证
     * @return 文件
     * @throws IOException
     *             文件不存在
     */
    File getFile(FileStorageMetadata metadata) throws IOException;

    /**
     * 根据文件凭证删除文件
     * 
     * @param metadata
     *            文件凭证
     * @throws IOException
     */
    void deleteFile(FileStorageMetadata metadata) throws IOException;

    /**
     * 文件存储器的存储类型
     * 
     * @return 存储类型
     */
    StorageType getStorageType();

    // 不适合ejb方法
    // FileStorageMetadata putFile(String fileName, InputStream in) throws IOException;
    // FileStorageMetadata putFile(String fileName, InputStream in, Map<String, Object> properties) throws IOException;
    // InputStream getInputStream(FileStorageMetadata metadata) throws IOException;

}
