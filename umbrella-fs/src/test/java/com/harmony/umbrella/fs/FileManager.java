package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;

/**
 * 文件管理,文件管理的统一入口
 * 
 * @author wuxii@foxmail.com
 */
public interface FileManager {

    /**
     * 上传文件
     * 
     * @param file
     *            文件
     * @return 上传后对应服务器上的文件
     */
    FileItem upload(File file) throws IOException;

    /**
     * 指定存储类的文件上传
     * 
     * @param file
     *            文件
     * @param storageType
     *            存储类型
     * @return 上传后对应服务器上的文件
     */
    FileItem upload(File file, StorageType storageType) throws IOException;

    /**
     * 指定文件名来上传文件
     * 
     * @param fileName
     *            文件名
     * @param file
     *            文件
     * @return 上传后对应服务器上的文件
     */
    FileItem upload(String fileName, File file) throws IOException;

    /**
     * 指定文件存储类型以及并存来上传文件
     * 
     * @param fileName
     *            文件名
     * @param file
     *            文件
     * @param storageType
     *            文件存储类型
     * @return 上传后对应服务器上的文件
     */
    FileItem upload(String fileName, File file, StorageType storageType) throws IOException;

    /**
     * 根据文件上传的id来下载文件
     * 
     * @param fileId
     *            文件id
     * @return 上传后对应服务器上的文件
     */
    FileItem download(Long fileId) throws IOException;

    /**
     * 删除文件
     * 
     * @param fileId
     *            文件item id
     */
    void delete(Long... fileId) throws IOException;

    /**
     * 批量删除文件
     * 
     * @param fileItem
     *            文件item
     */
    void delete(FileItem... fileItem) throws IOException;

    // 不适合ejb方法

    // FileItem upload(String fileName, InputStream in) throws IOException;
    // FileItem upload(String fileName, InputStream in, StorageType storageType) throws IOException;
}