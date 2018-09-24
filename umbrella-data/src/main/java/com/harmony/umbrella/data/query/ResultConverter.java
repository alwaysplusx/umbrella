package com.harmony.umbrella.data.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wuxii
 */
class ResultConverter<T> implements Function<Result, T> {

    public static final <T> ResultConverter<T> convertFor(Class<T> resultClass) {
        return new ResultConverter<>(resultClass);
    }

    private final Class<T> resultClass;

    public ResultConverter(Class<T> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public T apply(Result result) {
        Map<String, Object> map = new HashMap<>();
        for (RowResult row : result) {
            String name = row.getName();
            applyValue(map, name, row.getValue());
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

}
