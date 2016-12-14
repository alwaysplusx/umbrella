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
     * 是否允许空条件的查询, 对于查询list数据时候尤为重要
     */
    ALLOW_EMPTY_CONDITION;

    private QueryFeature() {
        mask = (1 << ordinal());
    }

    private final int mask;

    public final int getMask() {
        return mask;
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
