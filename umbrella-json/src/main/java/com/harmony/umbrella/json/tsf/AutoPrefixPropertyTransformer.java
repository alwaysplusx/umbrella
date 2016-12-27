package com.harmony.umbrella.json.tsf;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.harmony.umbrella.json.PropertyTransformer;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * 自动添加前缀的属性转化器, 转化器中通过类与属性的关系来确定转化关系, 如:
 * <ul>
 * <li>page下需要过滤的为: content[*].propertyName
 * <li>list/array下需要过滤的为: [*].propertyName
 * <li>单个实体需过滤的为: propertyName
 * </ul>
 * 在以上类型中调用端只关系具体内容的字段过滤. 如果想要过滤根下的字段可以通过配置{@code $.propertyName}来指定属性
 * 
 * @author wuxii@foxmail.com
 */
public class AutoPrefixPropertyTransformer implements PropertyTransformer {

    private static final Log log = Logs.getLog(AutoPrefixPropertyTransformer.class);

    private Map<Class, String> prefixMap = new HashMap<>();

    public AutoPrefixPropertyTransformer() {
    }

    public AutoPrefixPropertyTransformer(Map<Class, String> prefixMap) {
        this.prefixMap.putAll(prefixMap);
    }

    @Override
    public boolean support(Class<?> type) {
        return getPrefix(type) != null;
    }

    @Override
    public Collection<String> transform(Class<?> type, String... property) {
        final String prefix = getPrefix(type);
        if (prefix == null) {
            log.warn("not have {} property tranfsorm, just return original property", type);
            return Arrays.asList(property);
        }
        Set<String> result = new LinkedHashSet<>();
        for (String p : property) {
            if (p.startsWith("$.")) {
                result.add(p.substring(2));
            } else {
                result.add(prefix + p);
            }
        }
        return result;
    }

    public String getPrefix(Class<?> type) {
        if (prefixMap.containsKey(type)) {
            return prefixMap.get(type);
        }
        Set<Class> types = prefixMap.keySet();
        for (Class t : types) {
            if (t.isAssignableFrom(type)) {
                return prefixMap.get(t);
            }
        }
        return null;
    }

    public void map(Class<?> type, String prefix) {
        this.prefixMap.put(type, prefix);
    }

    @Override
    public Object clone() {
        return new AutoPrefixPropertyTransformer(prefixMap);
    }

}
