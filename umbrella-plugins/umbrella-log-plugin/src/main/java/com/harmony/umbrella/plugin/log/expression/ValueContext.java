package com.harmony.umbrella.plugin.log.expression;

import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public interface ValueContext {

    Object find(Expression exp);

    Map<String, Object> getInContext();

    Map<String, Object> getOutContext();

}
