package com.harmony.umbrella.config;

import java.math.BigDecimal;

/**
 * 配置管理的配置基础实现
 * 
 * @author wuxii@foxmail.com
 */
public class ParamEntry implements Param {

    /**
     * 配置key
     */
    protected String key;
    /**
     * 配置的value
     */
    protected Object value;
    /**
     * 配置描述
     */
    protected String description;

    public ParamEntry() {
    }

    public ParamEntry(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasValue() {
        return value != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return value != null ? value.toString() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getBoolean() {
        String value = getString();
        return value == null ? null : Boolean.valueOf(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte getByte() {
        Number number = getNumber();
        return number == null ? null : number.byteValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Character getCharacter() {
        String value = getString();
        return (value == null || value.length() != 1) ? null : value.charAt(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short getShort() {
        Number number = getNumber();
        return number == null ? null : number.shortValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInteger() {
        Number number = getNumber();
        return number == null ? null : number.intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLong() {
        Number number = getNumber();
        return number == null ? null : number.longValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getFloat() {
        Number number = getNumber();
        return number == null ? null : number.floatValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble() {
        Number number = getNumber();
        return number == null ? null : number.doubleValue();
    }

    private Number getNumber() {
        String value = getString();
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "{" + key + ":" + value + "}";
    }

}
