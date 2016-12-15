package com.harmony.umbrella.json.serializer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

/**
 * @author wuxii@foxmail.com
 */
public class TypeAttributeFilter implements PropertyPreFilter {

    private final Class<?> type;

    private final Set<String> propertyNames = new HashSet<>();

    private final boolean include;

    public TypeAttributeFilter(Class<?> type, FilterMode mode) {
        this(type, mode, new String[0]);
    }

    public TypeAttributeFilter(Class<?> type, String... names) {
        this(type, FilterMode.EXCLUDE, names);
    }

    public TypeAttributeFilter(Class<?> type, FilterMode mode, String... names) {
        this.type = type;
        this.include = FilterMode.INCLUDE == mode;
        this.propertyNames.addAll(Arrays.asList(names));
    }

    @Override
    public boolean apply(JSONSerializer serializer, Object object, String name) {
        if (!type.isInstance(object)) {
            return true;
        }
        return include ? propertyNames.contains(name) : !propertyNames.contains(name);
    }

    public Set<String> getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(Set<String> propertyNames) {
        this.propertyNames.clear();
        this.propertyNames.addAll(propertyNames);
    }

    public FilterMode getFilterMode() {
        return include ? FilterMode.INCLUDE : FilterMode.EXCLUDE;
    }
}
