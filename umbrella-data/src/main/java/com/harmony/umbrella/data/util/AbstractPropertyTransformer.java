package com.harmony.umbrella.data.util;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractPropertyTransformer implements TypedPropertyTransformer {

    @Override
    public String[] transform(String... property) {
        final String prefix = getPrefix();
        Set<String> result = new LinkedHashSet<>();
        for (String p : property) {
            if (p.startsWith("$.")) {
                result.add(p.substring(2));
            } else {
                result.add(prefix + p);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public abstract String getPrefix();

}