package com.harmony.umbrella.data.query;

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
     * 允许无条件的全表不分页查询
     */
    FULL_TABLE_QUERY,
    /**
     * 当无查询条件时采用conjunction为默认条件
     */
    CONJUNCTION;

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
