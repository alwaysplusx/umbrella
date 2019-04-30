package com.harmony.umbrella.template.support;

import com.harmony.umbrella.template.Expressions;
import com.harmony.umbrella.template.ExpressionsCache;

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
    public void removeExpressionsFromCache(String text) {

    }

    @Override
    public void putExpressionsInCache(Expressions expressions) {

    }

}
