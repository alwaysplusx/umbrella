package com.harmony.umbrella.json;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 序列化配置项
 * 
 * @author wuxii@foxmail.com
 */
public interface SerializerConfig {

    /**
     * 集合所有配置生成过滤器
     * 
     * @return 过滤器
     */
    SerializeFilter[] getFilters();

    /**
     * 序列化特性
     * 
     * @return 序列化特性
     */
    SerializerFeature[] getFeatures();

    /**
     * fastjson序列化配置
     * 
     * @return fastjson序列化配置
     */
    SerializeConfig getFastjsonSerializeConfig();

}