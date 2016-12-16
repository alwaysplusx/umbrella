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
