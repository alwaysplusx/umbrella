package com.harmony.umbrella.json.deserializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.util.MemberUtils;

/**
 * @author wuxii@foxmail.com
 */
public class CamelCaseNameProcessor implements ExtraProcessor {

    private final List<Converter> converters = new ArrayList<>();
    private final char link;
    private final boolean toUpperCase;

    public CamelCaseNameProcessor() {
        this('_', true);
    }

    public CamelCaseNameProcessor(char link, boolean toUpperCase, Converter<?, ?>... converters) {
        this.link = link;
        this.toUpperCase = toUpperCase;
        this.converters.addAll(Arrays.asList(converters));
    }

    @Override
    public void processExtra(Object object, String key, Object value) {
        if (value == null) {
            return;
        }
        Member member = getMember(key, object.getClass());
        if (member == null) {
            return;
        }
        Object destValue = null;
        if (value instanceof JSONArray) {
            final String jsonString = ((JSONArray) value).toJSONString();
            if (member.getType().isArray()) {
                destValue = JSON.parseObject(jsonString, member.getType(), this);
            } else if (List.class.isAssignableFrom(member.getType())) {
                // TODO 无法实现
                JSON.parseObject(jsonString, new TypeReference<List>() {
                }.getType(), this);
            }
        } else {
            convertValue(value, member.getType());
        }
        member.set(object, destValue);
    }

    private Member getMember(String name, Class<?> type) {
        StringBuilder o = new StringBuilder();
        for (int i = 0, max = name.length(); i < max; i++) {
            char c = name.charAt(i);
            if (link == c) {
                o.append(toUpperCase ? Character.toUpperCase(name.charAt(++i)) : name.charAt(++i));
            } else {
                o.append(c);
            }
        }
        return MemberUtils.accessMember(type, name);
    }

    public <T> T convertValue(Object fromValue, Class<T> type) throws IllegalArgumentException {
        if (type.isAssignableFrom(fromValue.getClass())) {
            return (T) fromValue;
        }
        return (T) fromValue;
    }

}
