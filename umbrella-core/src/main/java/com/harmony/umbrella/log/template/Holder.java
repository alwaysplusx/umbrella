package com.harmony.umbrella.log.template;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author wuxii@foxmail.com
 */
class Holder extends ArrayList<Object> {

    private static final long serialVersionUID = 1L;
    final Object target;
    final Object result;
    final Object[] arguments;

    public Holder(Object target, Object result, Object[] arguments) {
        super(Arrays.asList(arguments));
        this.target = target;
        this.result = result;
        this.arguments = arguments;
    }

    public Object get$() {
        return target;
    }

    public Object getTarget() {
        return target;
    }

    public Object getResult() {
        return result;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Object[] getArgs() {
        return arguments;
    }

    public Object[] getArg() {
        return arguments;
    }

}
