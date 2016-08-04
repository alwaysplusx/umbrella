package com.harmony.umbrella.config;

/**
 * 
 * 配置管理的抽象实现
 * 
 * @author wuxii@foxmail.com
 * @see ParamManager
 */
public abstract class AbstractParamManager implements ParamManager {

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getBoolean(String key, Boolean def) {
        Param param = get(key);
        return param != null ? param.getBoolean() : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte getByte(String key) {
        return getByte(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte getByte(String key, Byte def) {
        Param param = get(key);
        return param != null ? param.getByte() : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Character getCharacter(String key) {
        return getCharacter(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Character getCharacter(String key, Character def) {
        Param param = get(key);
        return param != null ? param.getCharacter() : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble(String key, Double def) {
        Param param = get(key);
        return param != null ? param.getDouble() : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getFloat(String key, Float def) {
        Param param = get(key);
        return param != null ? param.getFloat() : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInteger(String key, Integer def) {
        Param param = get(key);
        return param != null ? param.getInteger() : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLong(String key, Long def) {
        Param param = get(key);
        return param != null ? param.getLong() : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short getShort(String key) {
        return getShort(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short getShort(String key, Short def) {
        Param param = get(key);
        return param != null ? param.getShort() : def;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String key, String def) {
        Param param = get(key);
        return param != null ? param.getString() : def;
    }
}
