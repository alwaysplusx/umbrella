/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.log.support;

import java.text.MessageFormat;

import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractLog implements Log {

    private final String className;

    private static final Object[] EMPTY_ARGS = new Object[0];

    protected boolean isTraceEnabled = false;
    protected boolean isDebugEnabled = false;
    protected boolean isInfoEnabled = false;
    protected boolean isWarnEnabled = true;
    protected boolean isErrorEnabled = true;

    public AbstractLog(String className) {
        this.className = className;
    }

    /**
     * 记录日志汇总入口
     * <p>
     * 此之前已经对带参数的message做了格式化处理
     * 
     * @param message
     * @param level
     * @param exception
     */
    protected abstract void log(String message, Level level, Throwable exception);

    protected void log(String message, Level level, Throwable exception, Object[] arguments) {
        message = format(message, arguments);
        if (exception == null && arguments.length != 0) {
            Object lastArgument = arguments[arguments.length - 1];
            if (lastArgument instanceof Throwable) {
                exception = (Throwable) lastArgument;
            }
        }
        log(message, level, exception);
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public boolean isTraceEnabled() {
        return isTraceEnabled;
    }

    @Override
    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public boolean isInfoEnabled() {
        return isInfoEnabled;
    }

    @Override
    public boolean isWarnEnabled() {
        return isWarnEnabled;
    }

    @Override
    public boolean isErrorEnabled() {
        return isErrorEnabled;
    }

    /**
     * 扩展方法
     * <p>
     * 现支持{@linkplain MessageFormat#format(String, Object...)}
     * 
     * @param msg
     * @param arguments
     */
    protected String format(String msg, Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return msg;
        }
        return MessageFormat.format(msg, arguments);
    }

    @Override
    public void trace(Object msg) {
        if (isTraceEnabled()) {
            log(String.valueOf(msg), Level.TRACE, null, EMPTY_ARGS);
        }
    }

    @Override
    public void trace(String msg, Object... arguments) {
        if (isTraceEnabled()) {
            log(msg, Level.TRACE, null, arguments);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            log(msg, Level.TRACE, t, EMPTY_ARGS);
        }
    }

    @Override
    public void debug(Object msg) {
        if (isDebugEnabled()) {
            log(String.valueOf(msg), Level.DEBUG, null, EMPTY_ARGS);
        }
    }

    @Override
    public void debug(String msg, Object... arguments) {
        if (isDebugEnabled()) {
            log(msg, Level.DEBUG, null, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            log(msg, Level.DEBUG, t, EMPTY_ARGS);
        }
    }

    @Override
    public void info(Object msg) {
        if (isInfoEnabled()) {
            log(String.valueOf(msg), Level.INFO, null, EMPTY_ARGS);
        }
    }

    @Override
    public void info(String msg, Object... arguments) {
        if (isInfoEnabled()) {
            log(msg, Level.INFO, null, arguments);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            log(msg, Level.INFO, t, EMPTY_ARGS);
        }
    }

    @Override
    public void warn(Object msg) {
        if (isWarnEnabled()) {
            log(String.valueOf(msg), Level.WARN, null, EMPTY_ARGS);
        }
    }

    @Override
    public void warn(String msg, Object... arguments) {
        if (isWarnEnabled()) {
            log(msg, Level.WARN, null, arguments);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            log(msg, Level.WARN, t, EMPTY_ARGS);
        }
    }

    @Override
    public void error(Object msg) {
        if (isErrorEnabled()) {
            log(String.valueOf(msg), Level.ERROR, null, EMPTY_ARGS);
        }
    }

    @Override
    public void error(String msg, Object... arguments) {
        if (isErrorEnabled()) {
            log(msg, Level.ERROR, null, arguments);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            log(msg, Level.ERROR, t, EMPTY_ARGS);
        }
    }

}
