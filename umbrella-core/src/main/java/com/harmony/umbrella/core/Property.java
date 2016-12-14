package com.harmony.umbrella.core;

/**
 * 配置信息， 配置信息只支持元数据类型
 * 
 * @author wuxii@foxmail.com
 */
public interface Property {

    /**
     * 配置信息所对应的key
     * 
     * @return key
     */
    String getKey();

    /**
     * 配置值
     * 
     * @see #getString()
     * @return param value
     */
    Object getValue();

    /**
     * 检查是否有值
     * 
     * @return boolean
     */
    boolean hasValue();

    /**
     * 配置的文本值
     * 
     * @return string value
     */
    String getString();

    /**
     * 配置的布尔值
     * 
     * @return boolean value
     */
    Boolean getBoolean();

    /**
     * 配置的比特值
     * 
     * @return byte value
     */
    Byte getByte();

    /**
     * 字符值
     * 
     * @return character
     */
    Character getCharacter();

    /**
     * short 值，如果对应的value不是数字型返回null
     * 
     * @return short
     */
    Short getShort();

    /**
     * integer值,如果对应的value不是数字型返回null
     * 
     * @return interger
     */
    Integer getInteger();

    /**
     * long,如果对应的value不是数字型返回null
     * 
     * @return long
     */
    Long getLong();

    /**
     * float,如果对应的value不是数字型返回null
     * 
     * @return float
     */
    Float getFloat();

    /**
     * double, 如果对应的value不是数字型返回null
     * 
     * @return double
     */
    Double getDouble();

    /**
     * 配置信息的描述
     * 
     * @return param description
     */
    String getDescription();

}
