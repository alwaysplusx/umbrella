package com.harmony.umbrella.config;

import java.util.List;

/**
 * 配置管理器
 * <p>
 * 
 * 配置参数类型
 * 
 * @author wuxii@foxmail.com
 */
public interface ParamManager {

    /**
     * 根据key获取对应的配置参数
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Param get(String key);

    /**
     * 设置配置参数
     * 
     * @param param
     */
    void set(Param param);

    /**
     * 根据相同的key开头获取配置参数
     * 
     * @param prefix
     *            配置参数前缀
     * @return
     */
    List<Param> getStartWith(String prefix);

    /**
     * 根据key获取配置参数的boolean值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Boolean getBoolean(String key);

    /**
     * 根据key获取配置参数的boolean值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Boolean getBoolean(String key, Boolean def);

    /**
     * 根据key获取配置参数的byte值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Byte getByte(String key);

    /**
     * 根据key获取配置参数的byte值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Byte getByte(String key, Byte def);

    /**
     * 根据key获取配置参数的Character值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Character getCharacter(String key);

    /**
     * 根据key获取配置参数的Character值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Character getCharacter(String key, Character def);

    /**
     * 根据key获取配置参数的Double值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Double getDouble(String key);

    /**
     * 根据key获取配置参数的Double值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Double getDouble(String key, Double def);

    /**
     * 根据key获取配置参数的Float值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Float getFloat(String key);

    /**
     * 根据key获取配置参数的Float值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Float getFloat(String key, Float def);

    /**
     * 根据key获取配置参数的Integer值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Integer getInteger(String key);

    /**
     * 根据key获取配置参数的Integer值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Integer getInteger(String key, Integer def);

    /**
     * 根据key获取配置参数的Long值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Long getLong(String key);

    /**
     * 根据key获取配置参数的Long值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Long getLong(String key, Long def);

    /**
     * 根据key获取配置参数的Short值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Short getShort(String key);

    /**
     * 根据key获取配置参数的Short值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    Short getShort(String key, Short def);

    /**
     * 根据key获取配置参数的String值， 如果key对应的param未找到返回null
     * 
     * @param key
     *            配置参数key
     * @return
     */
    String getString(String key);

    /**
     * 根据key获取配置参数的String值， 如果key对应的param未找到返回默认值
     * 
     * @param key
     *            配置参数key
     * @return
     */
    String getString(String key, String def);

}
