package com.harmony.umbrella.excel.cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ClassUtils;

import com.harmony.umbrella.excel.CellResolver;

/**
 * @author wuxii@foxmail.com
 */
public class CellResolverFactory {

    protected static final List<CellResolver> RESOLVERS = new ArrayList<CellResolver>();

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }

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

    public static CellResolver<?> findCellResolver(Class<?> targetType, CellResolver[] cellResolvers) {
        return findCellResolver(targetType, Arrays.asList(cellResolvers));
    }

    public static CellResolver<?> findCellResolver(Class<?> targetType, List<CellResolver> cellResolvers) {
        // in, long等类型转为包装类
        targetType = targetType.isPrimitive() ? primitiveTypeToWrapperMap.get(targetType) : targetType;
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
