/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.harmony.umbrella.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 关于异常的工具类.
 * <p/>
 * 参考了guava的Throwables。
 *
 * @author calvin
 */
public abstract class Exceptions {

    /**
     * 将CheckedException转换为UncheckedException.
     */
    public static RuntimeException unchecked(Throwable ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        } else {
            return new RuntimeException(ex);
        }
    }

    /**
     * 创建UncheckedException
     */
    public static RuntimeException unchecked(String message, Throwable ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        } else {
            return new RuntimeException(message, ex);
        }
    }

    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 获取异常的Root Cause.
     */
    public static Throwable getRootCause(Throwable ex) {
        Throwable cause;
        while ((cause = ex.getCause()) != null) {
            ex = cause;
        }
        return ex;
    }

    /**
     * 获取所有异常堆栈
     *
     * @param ex
     *         异常
     * @return 异常的Cause
     */
    public static Throwable[] getAllCause(Throwable ex) {
        List<Throwable> result = new LinkedList<Throwable>();
        while (ex.getCause() != null) {
            ex = ex.getCause();
            result.add(ex);
        }
        return result.toArray(new Throwable[result.size()]);
    }

    /**
     * 获取所有异常的信息
     *
     * @param ex
     *         异常
     * @return 所有异常的信息
     */
    public static String getAllMessage(Throwable ex) {
        StringBuilder buffer = new StringBuilder();
        Iterator<Throwable> iterator = Arrays.asList(getAllCause(ex)).iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }
}
