package com.harmony.umbrella.excel.cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.harmony.umbrella.excel.CellResolver;
import com.harmony.umbrella.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
public class CellResolverFactory {

    @SuppressWarnings("rawtypes")
    protected static final List<CellResolver> RESOLVERS = new ArrayList<CellResolver>();

    static {
        RESOLVERS.add(StringCellResolver.INSTANCE);
        RESOLVERS.add(IntegerCellResolver.INSTANCE);
        RESOLVERS.add(DateCellResolver.INSTANCE);
        RESOLVERS.add(CalendarCellResolver.INSTANCE);
        RESOLVERS.add(LongCellResolver.INSTANCE);
        RESOLVERS.add(BooleanCellResolver.INSTANCE);
        RESOLVERS.add(ByteCellResolver.INSTANCE);
        RESOLVERS.add(DoubleCellResolver.INSTANCE);
        RESOLVERS.add(FloatCellResolver.INSTANCE);
        RESOLVERS.add(NumberCellResolver.INSTANCE);
        RESOLVERS.add(ShortCellResolver.INSTANCE);
    }

    public static CellResolver<?> createCellResolver(Class<?> targetType) {
        return findCellResolver(targetType, RESOLVERS);
    }

    @SuppressWarnings("rawtypes")
    public static CellResolver<?> findCellResolver(Class<?> targetType, CellResolver[] cellResolvers) {
        return findCellResolver(targetType, Arrays.asList(cellResolvers));
    }

    @SuppressWarnings("rawtypes")
    public static CellResolver<?> findCellResolver(Class<?> targetType, List<CellResolver> cellResolvers) {
        // in, long等类型转为包装类
        targetType = targetType.isPrimitive() ? ClassUtils.getPrimitiveWrapperType(targetType) : targetType;
        for (CellResolver cellResolver : cellResolvers) {
            // 一级匹配，目标与需求直接相等
            if (targetType == cellResolver.targetType()) {
                return cellResolver;
            }
        }
        for (CellResolver<?> cellResolver : cellResolvers) {
            // 二级匹配，目标是需求的子类
            if (ClassUtils.isAssignable(targetType, cellResolver.targetType())) {
                return cellResolver;
            }
        }
        return null;
    }
}
