package com.harmony.umbrella.json;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.NameFilter;

/**
 * @author wuxii@foxmail.com
 */
public enum KeyStyle implements NameFilter {

    NONE(null),
    CAMEL_CASE(PropertyNamingStrategy.CamelCase),
    PASCAL_CASE(PropertyNamingStrategy.PascalCase),
    SNAKE_CASE(PropertyNamingStrategy.SnakeCase),
    KEBAB_CASE(PropertyNamingStrategy.KebabCase);

    private PropertyNamingStrategy strategy;

    KeyStyle(PropertyNamingStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String process(Object object, String name, Object value) {
        return strategy == null ? name : strategy.translate(name);
    }

    public PropertyNamingStrategy namingStrategy() {
        return strategy;
    }

}
