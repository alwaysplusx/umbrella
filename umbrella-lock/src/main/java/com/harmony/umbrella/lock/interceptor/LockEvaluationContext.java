package com.harmony.umbrella.lock.interceptor;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

class LockEvaluationContext extends MethodBasedEvaluationContext {

    LockEvaluationContext(Object rootObject, Method method, Object[] arguments,
                          ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
    }

    @Override
    @Nullable
    public Object lookupVariable(String name) {
        return super.lookupVariable(name);
    }

}
