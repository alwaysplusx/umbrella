package com.harmony.umbrella.query.support;

import lombok.Getter;

import java.io.Serializable;

/**
 * copy from jdk
 *
 * @see java.lang.invoke.SerializedLambda
 */
@Getter
public class SerializedLambda implements Serializable {

    private static final long serialVersionUID = 8025925345765570181L;

    private Class<?> capturingClass;
    private String functionalInterfaceClass;
    private String functionalInterfaceMethodName;
    private String functionalInterfaceMethodSignature;
    private String implClass;
    private String implMethodName;
    private String implMethodSignature;
    private int implMethodKind;
    private String instantiatedMethodType;
    private Object[] capturedArgs;

    @Override
    public String toString() {
        return implClass + "#" + implMethodName;
    }

}
