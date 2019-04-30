package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface ExpressionsCache {

    // TODO 考虑分割符
    Expressions getExpressionsFromCache(String text);

    void removeExpressionsFromCache(String text);

    void putExpressionsInCache(Expressions expressions);

}