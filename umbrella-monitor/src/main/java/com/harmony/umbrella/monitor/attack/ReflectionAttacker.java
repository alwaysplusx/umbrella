/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.monitor.attack;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.monitor.Attacker;
import com.harmony.umbrella.util.MethodUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ReflectionAttacker implements Attacker<Object> {

    @Override
    public Map<String, Object> attack(Object target, String... names) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String name : names) {
            try {
                Method method = MethodUtils.findReadMethod(target.getClass(), name);
                result.put(name, MethodUtils.invokeMethod(method, target));
            } catch (Exception e) {
            }
        }
        return result;
    }
}