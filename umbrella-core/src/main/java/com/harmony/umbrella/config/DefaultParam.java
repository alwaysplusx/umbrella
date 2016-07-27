package com.harmony.umbrella.config;

import java.math.BigDecimal;

/**
 * 配置管理的配置基础实现
 * 
 * @author wuxii@foxmail.com
 */
public class DefaultParam implements Param {

    /**
     * 配置key
     */
    protected String key;
    /**
     * 配置的value
     */
    protected Object value;
    /**
     * 值类型
     */
    protected String type;
    /**
     * 配置描述
     */
    protected String description;

    public DefaultParam() {
    }

    public DefaultParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public DefaultParam(String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    private Number getNumber() {
        String value = getString();
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return null;
        }
    }

    public Class<?> getJavaType() {
        if (type == null) {
            return null;
        }
        Class<?> typeClass = null;
        try {
            typeClass = Class.forName(type);
        } catch (Exception e) {
        }
        if (typeClass == null) {
            if ("string".equalsIgnoreCase(type)) {
                return String.class;
            } else if ("int".equalsIgnoreCase(type)) {
                return Integer.TYPE;
            } else if ("Interger".equalsIgnoreCase(type)) {
                return Integer.class;
            } else if ("long".equals(type)) {
                return Long.TYPE;
            } else if ("Long".equalsIgnoreCase(type)) {
                return Long.class;
            } else if ("double".equals(type)) {
                return Double.TYPE;
            } else if ("Double".equalsIgnoreCase(type)) {
                return Double.class;
            } else if ("boolean".equals(type)) {
                return Boolean.TYPE;
            } else if ("Boolean".equalsIgnoreCase(type)) {
                return Boolean.class;
            }
        }
        return typeClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

}
