package com.harmony.umbrella.data;

/**
 * 查询特性
 * 
 * @author wuxii@foxmail.com
 */
public enum QueryFeature {

    /**
     * select distinct * from xx_table;
     */
    DISTINCT,

    /**
     * 允许无条件的全表查询
     */
    ALLOW_LIST_QUERY_WHEN_EMPTY_CONDITION,
    /**
     * 当无查询条件时采用conjunction为默认条件
     */
    CONJUNCTION_WHEN_EMPTY_CONDITION,
    /**
     * 当无查询条件时候采用disjunction为默认条件
     */
    DISJUNCTION_WHEN_EMPTY_CONDITION;

    private QueryFeature() {
        mask = (1 << ordinal());
    }

    private final int mask;

    public final int getMask() {
        return mask;
    }

    public final boolean isEnable(int mask) {
        return isEnabled(mask, this);
    }

    public static boolean isEnabled(int features, QueryFeature feature) {
        return (features & feature.getMask()) != 0;
    }

    public static boolean isEnabled(int features, int fieaturesB, QueryFeature feature) {
        int mask = feature.getMask();

        return (features & mask) != 0 || (fieaturesB & mask) != 0;
    }

    public static int config(int features, QueryFeature feature, boolean state) {
        if (state) {
            features |= feature.getMask();
        } else {
            features &= ~feature.getMask();
        }

        return features;
    }

    public static int of(QueryFeature[] features) {
        if (features == null) {
            return 0;
        }

        int value = 0;

        for (QueryFeature feature : features) {
            value |= feature.getMask();
        }

        return value;
    }
}
