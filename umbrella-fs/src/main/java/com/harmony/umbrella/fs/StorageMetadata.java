package com.harmony.umbrella.fs;

import java.io.Serializable;
import java.util.Map;

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
     * 获取存储后在服务器上的路径
     * 
     * @return 存储路径
     */
    String getPath();

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
    String getStorageType();

    /**
     * 文件存储的扩展属性
     * 
     * @return 扩展属性
     */
    Map<String, Object> getProperties();

}