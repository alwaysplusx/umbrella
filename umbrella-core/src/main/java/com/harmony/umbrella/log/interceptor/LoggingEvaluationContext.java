package com.harmony.umbrella.log.interceptor;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 * @author wuxii
 */
public class LoggingEvaluationContext extends MethodBasedEvaluationContext {

    public LoggingEvaluationContext(Object rootObject, Method method,
                                    Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
    }

    public LoggingEvaluationContext(Object rootObject, Method method, Object[] arguments) {
        super(rootObject, method, arguments, new DefaultParameterNameDiscoverer());
    }

}
