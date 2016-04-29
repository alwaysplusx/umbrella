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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.harmony.umbrella.access.Member;
import com.harmony.umbrella.access.MemberAccess;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.validator.Validators;
import com.harmony.umbrella.ws.annotation.Key;

/**
 * 服务端的简单数据校验工具类,实际校验依赖于{@linkplain javax.validation.Validator}
 *
 * @author wuxii@foxmail.com
 * @see Validators
 */
public class ServiceUtils {

    public static Member[] getKeyMembers(Class<?> targetClass) {
        List<Member> result = new ArrayList<Member>();
        // 配置有@Key的get方法
        for (Method method : targetClass.getMethods()) {
            Key ann = method.getAnnotation(Key.class);
            if (ann != null && ReflectionUtils.isReadMethod(method)) {
                result.add(MemberAccess.access(targetClass, method));
            }
        }
        // 配置有@Key的字段
        for (Field field : targetClass.getDeclaredFields()) {
            Key ann = field.getAnnotation(Key.class);
            if (ann != null) {
                result.add(MemberAccess.access(targetClass, field));
            }
        }
        return result.toArray(new Member[result.size()]);
    }

    public static void sortMember(Member[] keyAccessMembers) {
        Arrays.sort(keyAccessMembers, new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                Key a1 = getKeyAnnotation(o1);
                Key a2 = getKeyAnnotation(o2);
                return (a1 == null) ? -1 : (a2 == null) ? 1 : (a1 == null && a2 == null) ? 0 : (a1.ordinal() < a2.ordinal()) ? -1 : (a1.ordinal() == a2
                        .ordinal()) ? 0 : 1;
            }
        });
    }

    /**
     * method 优先
     * 
     * @param member
     * @return
     */
    public static Key getKeyAnnotation(Member member) {
        Method method = member.getReadMethod();
        Key ann = null;
        if (method != null) {
            ann = method.getAnnotation(Key.class);
            if (ann == null) {
                Field field = member.getField();
                if (field != null) {
                    ann = field.getAnnotation(Key.class);
                }
            }
        }
        return ann;
    }

    public static String getKeyName(Member member) {
        Key ann = getKeyAnnotation(member);
        return ann != null ? ann.name() : member.getName();
    }
}
