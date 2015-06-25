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

import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleExcludeFilter implements PropertyPreFilter {

    public static final String currentRef = "$";
    private final Class<?> clazz;
    private final Set<String> excludes = new HashSet<String>();

    public SimpleExcludeFilter(String... excludes) {
        this(null, excludes);
    }

    public SimpleExcludeFilter(Class<?> clazz, String... properties) {
        super();
        this.clazz = clazz;
        for (String item : properties) {
            if (item != null) {
                this.excludes.add(item);
            }
        }
    }

    @Override
    public boolean apply(JSONSerializer serializer, Object source, String name) {
        if (source == null) {
            return true;
        }
        if (clazz != null && !clazz.isInstance(source)) {
            return true;
        }
        if (this.excludes.contains(name)) {
            return false;
        }
        return true;
    }

}
