package com.harmony.umbrella.json;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 序列化配置项
 * 
 * @author wuxii@foxmail.com
 */
public interface SerializerConfig<T extends SerializerConfig> {

    /**
     * 设置序列化的特性
     * 
     * @param feature
     *            特性
     * @return this
     */
    T withFeature(SerializerFeature... feature);

    /**
     * 自定义的序列化filter
     * 
     * @param filter
     *            自定义filter
     * @return this
     */
    T withFilter(SerializeFilter... filter);

    /**
     * 设置fastjson的过滤特性
     * 
     * @param config
     *            fastjson序列化特性
     * @return this
     */
    T withSerializeConfig(SerializeConfig config);

    /**
     * 当前序列化属性所配置的类
     * 
     * @return 配置目标类
     */
    Class<?> getType();

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
    SerializeConfig getSerializeConfig();

}