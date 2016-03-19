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

import static com.harmony.umbrella.monitor.annotation.HttpProperty.Scope.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.monitor.HttpAttacker;
import com.harmony.umbrella.monitor.annotation.HttpProperty.Scope;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleHttpAttacker implements HttpAttacker {

    @Override
    public Map<String, Object> attack(HttpServletRequest request, String... names) {
        return attack(request, PARAMETER, names);
    }

    @Override
    public Map<String, Object> attack(HttpServletRequest request, Scope scope, String... names) {
        if (names == null || names.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<String, Object>();

        if (PARAMETER.equals(scope)) {
            for (String name : names) {
                result.put(name, request.getParameter(name));
            }
        } else if (REQUEST.equals(scope)) {
            for (String name : names) {
                result.put(name, request.getAttribute(name));
            }
        } else if (SESSION.equals(scope)) {
            for (String name : names) {
                result.put(name, request.getSession().getAttribute(name));
            }
        }
        return result;
    }
}
