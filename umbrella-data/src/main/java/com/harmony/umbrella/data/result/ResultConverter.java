package com.harmony.umbrella.data.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wuxii
 */
class ResultConverter<T> implements Function<RowResult, T> {

    static <T> ResultConverter<T> convertFor(Class<T> resultClass) {
        return new ResultConverter<>(resultClass);
    }

    private final Class<T> resultClass;

    private ResultConverter(Class<T> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public T apply(RowResult row) {
        Map<String, Object> map = new HashMap<>();
        for (CellResult cell : row) {
            String name = cell.getName();
            applyValue(map, name, cell.getValue());
        }
        return JSON.toJavaObject(new JSONObject(map), resultClass);
    }

    private void applyValue(Map<String, Object> map, String path, Object value) {
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
