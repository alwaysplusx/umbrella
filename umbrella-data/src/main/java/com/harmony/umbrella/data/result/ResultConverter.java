package com.harmony.umbrella.data.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wuxii
 */
public class ResultConverter<T> implements Function<SelectionAndResult, T> {

    private Class<T> resultClass;
    private Map<String, String> nameMapping = new HashMap<>();

    public ResultConverter(Class<T> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public T apply(SelectionAndResult columnResults) {
        Map<String, Object> map = new HashMap<>();
        for (ColumnResult cr : columnResults) {
            String name = getMappingName(cr.getName());
            applyValue(map, name, cr.getResult());
        }
        return JSON.toJavaObject(new JSONObject(map), resultClass);
    }

    protected void applyValue(Map<String, Object> map, String path, Object value) {
        String[] names = path.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < names.length - 1; i++) {
            String name = names[i];
            Map<String, Object> temp = (Map<String, Object>) current.get(name);
            if (temp == null) {
                temp = new HashMap<>();
                current.put(name, temp);
            }
            current = temp;
        }
        current.put(names[names.length - 1], value);
    }

    protected String getMappingName(String name) {
        return nameMapping.getOrDefault(name, name);
    }

}
