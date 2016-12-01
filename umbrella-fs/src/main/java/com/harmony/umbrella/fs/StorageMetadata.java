package com.harmony.umbrella.fs;

import java.io.Serializable;
import java.util.Properties;

/**
 * 文件存储的凭证
 * 
 * @author wuxii@foxmail.com
 */
public interface StorageMetadata extends Serializable {

    /**
     * 存储后的名称
     * 
     * @return 存储后的名称
     */
    String getName();

    /**
     * 存储前的名称
     * 
     * @return 存储前的名称
     */
    String getOriginalName();

    /**
     * 获取存储后在服务器上的路径
     * 
     * @return 存储路径
     */
    String getPath();

    /**
     * 存储前的路径
     * 
     * @return 存储前的路径
     */
    String getOriginalPath();

    /**
     * 文件内容长度
     * 
     * @return 文件内容长度
     * @see java.io.InputStream#available()
     */
    long getContentLength();

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
    Properties getProperties();

}