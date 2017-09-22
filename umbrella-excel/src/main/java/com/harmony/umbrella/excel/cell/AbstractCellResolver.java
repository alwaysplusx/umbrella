package com.harmony.umbrella.excel.cell;

import com.harmony.umbrella.excel.CellResolver;
import com.harmony.umbrella.util.GenericUtils;

/**
 * 针对泛型做的类型匹配
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractCellResolver<T> implements CellResolver<T> {

    private Class<T> targetType;

    @Override
    public Class<T> targetType() {
        if (targetType == null) {
            targetType = (Class<T>) GenericUtils.getTargetGeneric(getClass(), AbstractCellResolver.class, 0);
        }
        return targetType;
    }

}
