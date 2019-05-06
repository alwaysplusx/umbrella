package com.harmony.umbrella.log.interceptor;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 * @author wuxii
 */
class LogEvaluationContext extends MethodBasedEvaluationContext {

    public LogEvaluationContext(Object rootObject, Method method,
                                Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
    }

    public LogEvaluationContext(Object rootObject, Method method, Object[] arguments) {
        super(rootObject, method, arguments, new DefaultParameterNameDiscoverer());
    }

}
