package com.harmony.umbrella.log.expression;

import java.util.Map;

import com.harmony.umbrella.log.annotation.Scope;

/**
 * @author wuxii@foxmail.com
 */
public interface ValueContext {

    Object find(String exp, Scope scope);

    Object find(Expression exp);

    Map<String, Object> getInContext();

    Map<String, Object> getOutContext();

}
