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
package com.harmony.umbrella.excel.support;

import java.lang.reflect.ParameterizedType;

import com.harmony.umbrella.excel.CellResolver;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractCellResolver<T> implements CellResolver<T> {

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getTargetType() {
        ParameterizedType pt = findParameterizedType(getClass());
        if (pt != null) {
            return (Class<T>) pt.getActualTypeArguments()[0];
        }
        throw new UnsupportedOperationException();
    }

    protected Class<?> getGenericSuperClass() {
        return AbstractCellResolver.class;
    }

    protected final ParameterizedType findParameterizedType(Class<?> subClass) {
        Class<?> superclass = subClass.getSuperclass();
        if (superclass == Object.class) {
            return null;
        } else if (getGenericSuperClass().equals(superclass)) {
            return (ParameterizedType) subClass.getGenericSuperclass();
        }
        return findParameterizedType(superclass);
    }

}
