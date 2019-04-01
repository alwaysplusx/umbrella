package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface ExpressionCache {

    ExpressionTemplate getTemplateFromCache();

    void putTemplateInCache(ExpressionTemplate template);

}