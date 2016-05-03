/*
 * Copyright 2012-2016 the original author or authors.
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
package com.harmony.umbrella.el.resolver;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.DigitUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PrimitiveArrayResolver extends CheckedResolver<Object> {

    private static final Map<Class<?>, Class<?>> primitiveArray = new HashMap<Class<?>, Class<?>>();

    static {
        primitiveArray.put(boolean[].class, Boolean[].class);
        primitiveArray.put(byte[].class, Byte[].class);
        primitiveArray.put(char[].class, Character[].class);
        primitiveArray.put(double[].class, Double[].class);
        primitiveArray.put(float[].class, Float[].class);
        primitiveArray.put(int[].class, Integer[].class);
        primitiveArray.put(long[].class, Long[].class);
        primitiveArray.put(short[].class, Short[].class);
    }

    public PrimitiveArrayResolver(int priority) {
        super(priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return obj != null && DigitUtils.isDigit(name) && primitiveArray.containsKey(obj.getClass());
    }

    @Override
    protected Object doResolve(String name, Object obj) {
        return Array.get(obj, Integer.valueOf(name));
    }

}
