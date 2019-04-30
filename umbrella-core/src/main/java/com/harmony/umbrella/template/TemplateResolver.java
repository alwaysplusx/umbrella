package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface TemplateResolver {

    <T extends Expression> Template<T> resolve(T expression);

}
