/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.ws.ser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.MethodUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.validator.ValidVisitor;
import com.harmony.umbrella.validator.util.ValidatorUtils;
import com.harmony.umbrella.ws.Key;

/**
 * 服务端的简单数据校验工具类,实际校验依赖于{@linkplain javax.validation.Validator}
 * 
 * @author wuxii@foxmail.com
 * @see ValidatorUtils
 */
public abstract class ServerValidation {

    private static final Logger log = LoggerFactory.getLogger(ServerValidation.class);

    public static boolean isValid(Object obj, MessageContent content) {
        return isValid(obj, content, Default.class);
    }

    public static boolean isValid(Object obj, MessageContent content, Class<?>... groups) {
        return isValid(obj, content, null, groups);
    }

    public static <T> boolean isValid(T obj, MessageContent content, ValidVisitor visitor, Class<?>... groups) {
        Assert.notNull(content, "message content must not be null");
        if (obj == null) {
            content.append("NULL", "input is null");
            return false;
        }
        String key = extractKey(obj);
        String message = ValidatorUtils.getViolationMessage(obj, visitor, groups);
        if (StringUtils.isNotBlank(message)) {
            content.append(key, message);
        }
        return content.containsKey(key);
    }

    public static String extractKey(Object obj, MemberInvoker[] memberInvokers) {
        Map<String, Object> keys = new LinkedHashMap<String, Object>();
        for (MemberInvoker mi : memberInvokers) {
            try {
                keys.put(mi.keyName(), mi.invoker(obj));
            } catch (InvokeException e) {
                log.warn("invok key member " + mi + " failed");
            }
        }
        return Json.toJson(keys, SerializerFeature.WriteMapNullValue);
    }

    private static String extractKey(Object obj) {
        return extractKey(obj, getKeyMemberSortByKey(obj.getClass()));
    }

    public static MemberInvoker[] getKeyMemberSortByKey(Class<?> targetClass) {
        List<MemberInvoker> memberList = getKeyMemberList(targetClass);

        Collections.sort(memberList, new Comparator<MemberInvoker>() {
            @Override
            public int compare(MemberInvoker o1, MemberInvoker o2) {
                int i1 = o1.key.ordinal();
                int i2 = o2.key.ordinal();
                return Integer.valueOf(i1).compareTo(Integer.valueOf(i2));
            }
        });

        return memberList.toArray(new MemberInvoker[memberList.size()]);
    }

    public static MemberInvoker[] getKeyMembers(Class<?> targetClass) {
        List<MemberInvoker> memberList = getKeyMemberList(targetClass);
        return memberList.toArray(new MemberInvoker[memberList.size()]);
    }

    private static List<MemberInvoker> getKeyMemberList(Class<?> targetClass) {
        List<MemberInvoker> result = new ArrayList<MemberInvoker>();

        for (Method method : targetClass.getMethods()) {
            Key ann = method.getAnnotation(Key.class);
            if (ann != null) {
                result.add(new MemberInvoker(targetClass, method, ann));
            }
        }

        for (Field field : targetClass.getDeclaredFields()) {
            Key ann = field.getAnnotation(Key.class);
            if (ann != null) {
                result.add(new MemberInvoker(targetClass, field, ann));
            }
        }

        return result;
    }

    public static class MemberInvoker {

        private Key key;
        private Method method;
        private Field field;
        private final Class<?> targetClass;

        private MemberInvoker(Class<?> targetClass, Field field, Key key) {
            this.targetClass = targetClass;
            this.field = field;
            this.key = key;
        }

        private MemberInvoker(Class<?> targetClass, Method method, Key key) {
            this.targetClass = targetClass;
            this.method = method;
            this.key = key;
        }

        public Object invoker(Object target) throws InvokeException {
            if (!targetClass.isAssignableFrom(target.getClass())) {
                throw new IllegalArgumentException("target object not match");
            }
            Assert.isTrue(method != null || field != null, "target method or field not set");

            if (method != null) {
                try {
                    return method.invoke(target);
                } catch (Exception e) {
                }
            }

            try {
                method = MethodUtils.findReadMethod(targetClass, field);
                return MethodUtils.invokeMethod(method, target);
            } catch (NoSuchMethodException e1) {
                throw new InvokeException(e1);
            }

        }

        public String keyName() {
            String name = key.name();
            if ("".equals(name)) {
                if (field != null) {
                    name = field.getName();
                } else {
                    String methodName = method.getName();
                    name = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                }
            }
            return name;
        }
    }

}
