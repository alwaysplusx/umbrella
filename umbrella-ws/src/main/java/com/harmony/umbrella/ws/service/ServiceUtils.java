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
package com.harmony.umbrella.ws.service;

import com.harmony.umbrella.validator.util.ValidatorUtils;
import com.harmony.umbrella.ws.annotation.Key;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 服务端的简单数据校验工具类,实际校验依赖于{@linkplain javax.validation.Validator}
 *
 * @author wuxii@foxmail.com
 * @see ValidatorUtils
 */
public class ServiceUtils {

    public static KeyAccessMember[] getKeyAccessMember(Class<?> targetClass) {
        List<KeyAccessMember> result = new ArrayList<KeyAccessMember>();

        // 配置有@Key的get方法
        for (Method method : targetClass.getMethods()) {
            Key ann = method.getAnnotation(Key.class);
            if (ann != null && method.getParameterTypes().length == 0) {
                result.add(new KeyAccessMember(targetClass, method, ann));
            }
        }

        // 配置有@Key的字段
        for (Field field : targetClass.getDeclaredFields()) {
            Key ann = field.getAnnotation(Key.class);
            if (ann != null) {
                result.add(new KeyAccessMember(targetClass, field, ann));
            }
        }

        return result.toArray(new KeyAccessMember[result.size()]);
    }

    public static void sortKeyAccessMember(KeyAccessMember[] keyAccessMembers) {
        Arrays.sort(keyAccessMembers, new Comparator<KeyAccessMember>() {
            @Override
            public int compare(KeyAccessMember o1, KeyAccessMember o2) {
                return o1.getOrdinal() - o2.getOrdinal();
            }
        });
    }

}
