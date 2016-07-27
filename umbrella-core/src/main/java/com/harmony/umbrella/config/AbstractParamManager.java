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
        Param param = get(key);
        return param != null ? param.getBoolean() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getBoolean(String key, Boolean def) {
        Boolean val = getBoolean(key);
        return val == null ? def : val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte getByte(String key) {
        Param param = get(key);
        return param != null ? param.getByte() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte getByte(String key, Byte def) {
        Byte val = getByte(key);
        return val == null ? def : val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Character getCharacter(String key) {
        Param param = get(key);
        return param != null ? param.getCharacter() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Character getCharacter(String key, Character def) {
        Character val = getCharacter(key);
        return val == null ? def : val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble(String key) {
        Param param = get(key);
        return param != null ? param.getDouble() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble(String key, Double def) {
        Double val = getDouble(key);
        return val == null ? def : val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getFloat(String key) {
        Param param = get(key);
        return param != null ? param.getFloat() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getFloat(String key, Float def) {
        Float val = getFloat(key);
        return val == null ? def : val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInteger(String key) {
        Param param = get(key);
        return param != null ? param.getInteger() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInteger(String key, Integer def) {
        Integer val = getInteger(key);
        return val == null ? def : val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLong(String key) {
        Param param = get(key);
        return param != null ? param.getLong() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLong(String key, Long def) {
        Long val = getLong(key);
        return val == null ? def : val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short getShort(String key) {
        Param param = get(key);
        return param != null ? param.getShort() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short getShort(String key, Short def) {
        Short val = getShort(key);
        return val == null ? def : val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String key) {
        Param param = get(key);
        return param != null ? param.getString() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String key, String def) {
        String val = getString(key);
        return val == null ? def : val;
    }
}
