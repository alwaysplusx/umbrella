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
package com.harmony.umbrella.el;

import com.harmony.umbrella.util.GenericUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class CheckedResolver<T> implements TypedResolver, Comparable<CheckedResolver<?>> {

    private Class<T> type;
    private int priority;

    public CheckedResolver(int priority) {
        this(null, priority);
    }

    public CheckedResolver(Class<T> type, int priority) {
        this.type = type;
        this.priority = priority;
    }

    public abstract boolean support(String name, Object obj);

    protected abstract Object doResolve(String name, T obj);

    public int priority() {
        return priority;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getSupportType() {
        if (type == null) {
            type = (Class<T>) GenericUtils.getTargetGeneric(getClass(), CheckedResolver.class, 0);
        }
        return type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolve(String name, Object obj) {
        return doResolve(name, (T) obj);
    }

    @Override
    public int compareTo(CheckedResolver<?> o) {
        return (priority < o.priority) ? -1 : ((priority == o.priority) ? 0 : 1);
    }
}
