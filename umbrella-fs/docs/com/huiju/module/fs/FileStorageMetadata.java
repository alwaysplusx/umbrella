package com.huiju.module.fs;

import java.io.Serializable;
import java.util.Map;

/**
 * 文件存储的凭证
 * 
 * @author wuxii@foxmail.com
 */
public interface FileStorageMetadata extends Serializable {

    /**
     * 文件名称
     * 
     * @return 文件名称
     */
    String getFileName();

    /**
     * 文件来源路径
     * 
     * @return 文件源路径
     */
    String getOriginal();

    /**
     * 文件的目标路径
     * 
     * @return 目标路径
     */
    String getDestination();

    /**
     * 文件内容长度
     * 
     * @return 文件内容长度
     * @see java.io.FileInputStream#available()
     */
    Long getContentLength();

    /**
     * 文件存储类型
     * 
     * @return 文件存储类型
     */
    StorageType getStorageType();

    /**
     * 文件存储的扩展属性
     * 
     * @return 扩展属性
     */
    Map<String, Object> getProperties();

}
