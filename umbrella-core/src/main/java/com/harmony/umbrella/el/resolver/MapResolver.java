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

import java.util.Map;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class MapResolver extends CheckedResolver<Map<?, ?>> {

    public MapResolver(int priority) {
        super(priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return StringUtils.isNotBlank(name) && obj instanceof Map;
    }

    @Override
    protected Object doResolve(String name, Map<?, ?> obj) {
        return obj.get(name);
    }

}
