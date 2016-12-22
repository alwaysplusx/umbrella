package com.harmony.umbrella.json.serializer;

import com.alibaba.fastjson.serializer.NameFilter;

/**
 * @author wuxii@foxmail.com
 */
public class MyNameFilter implements NameFilter {

    @Override
    public String process(Object object, String name, Object value) {
        StringBuilder o = new StringBuilder();
        for (int i = 0, max = name.length(); i < max; i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                o.append("_").append(Character.toLowerCase(c));
            } else {
                o.append(c);
            }
        }
        return o.toString();
    }

}
