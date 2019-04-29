package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public class NoneExpressionsCache implements ExpressionsCache {

    public static final NoneExpressionsCache INSTANCE = new NoneExpressionsCache();

    private NoneExpressionsCache() {
    }

    @Override
    public Expressions getExpressionsFromCache(String text) {
        return null;
    }

    @Override
    public void putExpressionsInCache(Expressions expressions) {

    }

}
