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

import java.util.List;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.DigitUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ListResolver extends CheckedResolver<List<?>> {

    public ListResolver(Class<List<?>> type, int priority) {
        super(type, priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return DigitUtils.isDigit(name) && obj instanceof List;
    }

    @Override
    protected Object doResolve(String name, List<?> obj) {
        return obj.get(Integer.valueOf(name));
    }

}
