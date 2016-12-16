package com.harmony.umbrella.json.serializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

/**
 * TODO 以当前的TYPE为根节点来指定当前根下所需要过滤的patterns. 从而达到指定根下的指定pattern过滤
 * <p>
 * 但是在一般的序列化过程中, filter往往是针对一个特定的根下展开的是否有此必要?
 * 
 * 根据常用的需求来说, filter都是使用时候创建使用后销毁的策略.
 * 
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
        this(type, mode, Arrays.asList(names));
    }

    public TypeAttributeFilter(Class<?> type, FilterMode mode, Collection<String> names) {
        this.type = type;
        this.include = FilterMode.INCLUDE == mode;
        this.propertyNames.addAll(names);
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
