package com.harmony.umbrella.ws;

/**
 * 
 * 加载服务的元数据信息
 * 
 * @author wuxii@foxmail.com
 */
public interface MetadataLoader {

    /**
     * 根据指定的serviceClass加载指定的元数据
     * 
     * @param serviceClass
     *            服务类
     * @return {@linkplain Metadata}
     */
    Metadata loadMetadata(Class<?> serviceClass);

    /**
     * 根据指定的service类名加载元数据
     * 
     * @param serviceClassName
     *            类名
     * @return {@linkplain Metadata}
     */
    Metadata loadMetadata(String serviceClassName);

}
