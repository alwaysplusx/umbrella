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
package com.harmony.umbrella.json.serializer;

import java.util.Collection;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerialContext;

/**
 * 通过构建字段的全程来实现内中的字段的字段的过滤
 * 
 * <pre>
 * class A {
 *     B b;
 *     C[] cs;
 * }
 * 
 * class B {
 *     C c;
 *     A a;
 * }
 * 
 * class C {
 *     String name;
 * }
 * </pre>
 * 
 * 上述过滤A中的B的C中的name propertyName为 b.c.name
 * <p>
 * 如果存在数组如 B 则如 a.cs[x].name
 * 
 * @author wuxii@foxmail.com
 */
public abstract class PropertyNameFilter implements PropertyPreFilter {

    public abstract boolean filter(Object source, String propertyName);

    @Override
    public boolean apply(JSONSerializer serializer, Object source, String name) {
        if (source == null) {
            return true;
        }
        return filter(source, getPropertyName(serializer.getContext(), name));
    }

    /**
     * 反向追溯字段的全称
     * 
     * @param context
     *            当前的serialContext
     * @param name
     *            在serialContext的字段名称
     * @return 追溯的结果名称
     */
    protected String getPropertyName(SerialContext context, String name) {
        if (context == null) {
            return name;
        }

        // 对于数组类的字段进行组合
        StringBuilder buf = new StringBuilder();

        SerialContext currentContext = context;

        // <----------
        // a.b.c.d.e.f 这是一个反溯的过程 
        while (currentContext.getParent() != null) {

            Object currentFieldName = currentContext.getFieldName();

            SerialContext parentContext = currentContext.getParent();
            Object parent = parentContext.getObject();

            String fieldName;
            if ((parent instanceof Collection || parent.getClass().isArray()) && currentFieldName instanceof Number) {
                fieldName = "[" + currentFieldName + "]";
            } else {
                fieldName = String.valueOf(currentFieldName);
            }

            if (buf.indexOf("[") == 0) {
                buf.insert(0, fieldName);
            } else {
                buf.insert(0, fieldName).insert(fieldName.length(), '.');
            }
            // <- 反溯
            currentContext = parentContext;
        }

        return buf.append(name).toString();
    }

}
