package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface ExpressionsCache {

    Expressions getExpressionsFromCache(String text);

    void putExpressionsInCache(Expressions expressions);
    
}