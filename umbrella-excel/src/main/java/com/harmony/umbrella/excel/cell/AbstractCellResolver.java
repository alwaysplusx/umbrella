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
package com.harmony.umbrella.excel.cell;

import com.harmony.umbrella.excel.CellResolver;
import com.harmony.umbrella.util.GenericUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractCellResolver<T> implements CellResolver<T> {

    private Class<T> targetType;

    @Override
    public boolean isTargetType(Class<?> targetType) {
        // 直接通过泛型判断类型匹配
        return getTargetType() == targetType;
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getTargetType() {
        if (targetType == null) {
            targetType = (Class<T>) GenericUtils.getTargetGeneric(getClass(), AbstractCellResolver.class, 0);
        }
        return targetType;
    }

}
