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

import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.MessageFactory;
import com.harmony.umbrella.log.message.ParameterizedMessageFactory;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractLog implements Log {

    protected final String className;
    protected final MessageFactory messageFactory;

    protected boolean isTraceEnabled = false;
    protected boolean isDebugEnabled = true;
    protected boolean isInfoEnabled = true;
    protected boolean isWarnEnabled = true;
    protected boolean isErrorEnabled = true;

    public AbstractLog(String className) {
        this.className = className;
        this.messageFactory = createDefaultMessageFactory();
    }

    public AbstractLog(String className, MessageFactory messageFactory) {
        this.className = className;
        this.messageFactory = messageFactory;
    }

    protected MessageFactory createDefaultMessageFactory() {
        return new ParameterizedMessageFactory();
    }

    /**
     * 记录日志汇总入口
     * <p>
     * 此之前已经对带参数的message做了格式化处理
     *
     * @param level
     * @param message
     * @param t
     */
    protected abstract void logMessage(Level level, Message message, Throwable t);

    protected abstract void logMessage(Level level, LogInfo logInfo);

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

    protected void logIfEnable(Level level, Object message) {
        if (isEnable(level)) {
            Message msg = messageFactory.newMessage(message);
            logMessage(level, msg, msg.getThrowable());
        }
    }

    protected void logIfEnable(Level level, LogInfo logInfo) {
        if (isEnable(level)) {
            logMessage(level, logInfo);
        }
    }

    protected void logIfEnable(Level level, Object message, Throwable t) {
        if (isEnable(level)) {
            logMessage(level, messageFactory.newMessage(message), t);
        }
    }

    protected void logIfEnable(Level level, String message, Object... params) {
        if (isEnable(level)) {
            Message msg = messageFactory.newMessage(message, params);
            logMessage(level, msg, msg.getThrowable());
        }
    }

    protected void logIfEnable(Level level, String message, Throwable t) {
        if (isEnable(level)) {
            logMessage(level, messageFactory.newMessage(message), t);
        }
    }

    public boolean isEnable(Level level) {
        if (level == null) {
            return isInfoEnabled();
        }
        switch (level.getStandardLevel()) {
        case ERROR:
            return isErrorEnabled();
        case WARN:
            return isWarnEnabled();
        case INFO:
            return isInfoEnabled();
        case ALL:
        case TRACE:
            return isTraceEnabled();
        case DEBUG:
            return isDebugEnabled();
        case OFF:
            return false;
        }
        return isInfoEnabled();
    }

    @Override
    public void trace(Object msg) {
        logIfEnable(Level.TRACE, msg);
    }

    @Override
    public void trace(String msg, Object... arguments) {
        logIfEnable(Level.TRACE, msg, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logIfEnable(Level.TRACE, msg, t);
    }

    @Override
    public void trace(LogInfo logInfo) {
        logIfEnable(Level.TRACE, logInfo);
    }

    @Override
    public void debug(Object msg) {
        logIfEnable(Level.DEBUG, msg);
    }

    @Override
    public void debug(String msg, Object... arguments) {
        logIfEnable(Level.DEBUG, msg, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logIfEnable(Level.DEBUG, msg, t);
    }

    @Override
    public void debug(LogInfo logInfo) {
        logIfEnable(Level.DEBUG, logInfo);
    }

    @Override
    public void info(Object msg) {
        logIfEnable(Level.INFO, msg);
    }

    @Override
    public void info(String msg, Object... arguments) {
        logIfEnable(Level.INFO, msg, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logIfEnable(Level.INFO, msg, t);
    }

    @Override
    public void info(LogInfo logInfo) {
        logIfEnable(Level.INFO, logInfo);
    }

    @Override
    public void warn(Object msg) {
        logIfEnable(Level.WARN, msg);
    }

    @Override
    public void warn(String msg, Object... arguments) {
        logIfEnable(Level.WARN, msg, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logIfEnable(Level.WARN, msg, t);
    }

    @Override
    public void warn(LogInfo logInfo) {
        logIfEnable(Level.WARN, logInfo);
    }

    @Override
    public void error(Object msg) {
        logIfEnable(Level.ERROR, msg);
    }

    @Override
    public void error(String msg, Object... arguments) {
        logIfEnable(Level.ERROR, msg, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logIfEnable(Level.ERROR, msg, t);
    }

    @Override
    public void error(LogInfo logInfo) {
        logIfEnable(Level.ERROR, logInfo);
    }

    @Override
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

}
