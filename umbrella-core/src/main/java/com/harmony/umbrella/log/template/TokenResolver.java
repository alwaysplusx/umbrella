package com.harmony.umbrella.log.template;

/**
 * @author wuxii@foxmail.com
 */
public interface TokenResolver {

    boolean support(ScopeToken scopeToken);

    Object resolve(ScopeToken scopeToken, LoggingContext context);

}
