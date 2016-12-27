package com.harmony.umbrella.json.serializer;

import com.alibaba.fastjson.serializer.NameFilter;

/**
 * 驼峰命名字段的名称过滤器
 * 
 * @author wuxii@foxmail.com
 */
public class CamelCaseNameFilter implements NameFilter {

    private final String append;
    private final boolean toLowerCase;

    public CamelCaseNameFilter() {
        this("_", true);
    }

    public CamelCaseNameFilter(String append, boolean toLowerCase) {
        this.append = append;
        this.toLowerCase = toLowerCase;
    }

    @Override
    public String process(Object object, String name, Object value) {
        StringBuilder o = new StringBuilder();
        for (int i = 0, max = name.length(); i < max; i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                o.append(append).append(toLowerCase ? Character.toLowerCase(c) : c);
            } else {
                o.append(c);
            }
        }
        return o.toString();
    }

}
