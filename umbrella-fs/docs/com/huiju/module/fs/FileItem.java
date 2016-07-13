package com.huiju.module.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * 对应服务器的上已经上传的文件item
 * 
 * @author wuxii@foxmail.com
 */
public interface FileItem extends Serializable {

    /**
     * 文件id
     * 
     * @return 文件id
     */
    Long getFileId();

    /**
     * 文件名
     * 
     * @return 文件名
     */
    String getFileName();

    /**
     * 文件存储类型
     * 
     * @return 文件存储类型
     */
    StorageType getStorageType();

    /**
     * 文件的扩展名
     * 
     * @return 文件扩展名
     */
    String getExtension();

    /**
     * 文件输入流
     * 
     * @return 文件流
     * @throws IOException
     *             文件不存在
     */
    InputStream getInputStream() throws IOException;

    /**
     * 文件
     * 
     * @return 服务器上的文件
     * @throws IOException
     *             文件不存在
     */
    File getFile() throws IOException;

}
