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
package com.harmony.umbrella.log;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 * 统一日志消息
 *
 * @author wuxii@foxmail.com
 */
public class LogMessage {

    public static final Level DEFAULT_LEVEL = Level.INFO;

    public static final String LOGMESSAGE_FQNC = LogMessage.class.getName();

    private Log log;

    private String bizModule;
    private Serializable bizId;

    private String module;
    private String action;

    private String message;
    private Throwable exception;
    private Level level;

    private String operator;
    private Serializable operatorId;

    private Object result;
    private Calendar startTime;
    private Calendar finishTime;

    private String stack;
    private String threadName;

    private LogFormat formater;

    public LogMessage(Log log) {
        this.log = log;
    }

    private String getStack() {
        String stack = null;
        StackTraceElement[] elements = new Throwable().getStackTrace();

        for (int max = elements.length - 1, i = max; i > 0; i--) {
            if (elements[i].getClassName().equals(LOGMESSAGE_FQNC)) {
                stack = elements[i + 1].toString();
                break;
            }
        }

        return stack;
    }

    public static LogMessage create(Log log) {
        return new LogMessage(log);
    }

    /**
     * 设置业务数据的id
     *
     * @param bizId
     *            业务数据的id
     * @return current logMessage
     */
    public LogMessage bizId(Serializable bizId) {
        this.bizId = bizId;
        return this;
    }

    /**
     * 设置业务模块
     *
     * @param bizModule
     *            业务模块
     * @return current logMessage
     */
    public LogMessage bizModule(String bizModule) {
        this.bizModule = bizModule;
        return this;
    }

    /**
     * 设置日志消息
     *
     * @param message
     *            log message
     * @return current logMessage
     */
    public LogMessage message(String message) {
        this.message = message;
        return this;
    }

    /**
     * 带格式话的消息模式
     * <p>
     * 现只支持{@linkplain java.text.MessageFormat}方式
     *
     * @param message
     *            消息模版
     * @param args
     *            消息参数
     * @return
     */
    public LogMessage message(String message, Object... args) {
        if (message != null) {
            this.message = MessageFormat.format(message, args);
        }
        if (args[args.length - 1] instanceof Throwable) {
            this.exception = (Throwable) args[args.length - 1];
        }
        return this;
    }

    /**
     * 设置日志所属于的模块
     *
     * @param module
     * @return
     */
    public LogMessage module(String module) {
        this.module = module;
        return this;
    }

    /**
     * 设置日志所表示的动作
     *
     * @param action
     *            日志表示的动作
     * @return current logMessage
     */
    public LogMessage action(String action) {
        this.action = action;
        return this;
    }

    /**
     * 设置结果
     *
     * @param result
     *            结果
     * @return current logMessage
     */
    public LogMessage result(Object result) {
        this.result = result;
        return this;
    }

    public LogMessage exception(Throwable exception) {
        this.exception = exception;
        return this;
    }

    /**
     * 设置业务数据的操作人
     *
     * @param username
     *            操作人名称
     * @return current logMessage
     */
    public LogMessage operator(String username) {
        this.operator = username;
        return this;
    }

    public LogMessage operatorId(Serializable operatorId) {
        this.operatorId = operatorId;
        return this;
    }

    /**
     * 设置开始时间 startTime = Calendar.getInstance();
     *
     * @return current logMessage
     */
    public LogMessage start() {
        return start(Calendar.getInstance());
    }

    /**
     * 设置开始时间
     *
     * @param startTime
     *            开始时间
     * @return current logMessage
     */
    public LogMessage start(Calendar startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * 设置结束时间 finishTime = Calendar.getInstance();
     *
     * @return current logMessage
     */
    public LogMessage finish() {
        return finish(Calendar.getInstance());
    }

    /**
     * 设置结束时间
     *
     * @param finishTime
     *            结束时间
     * @return current logMessage
     */
    public LogMessage finish(Calendar finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    /**
     * 设置日志级别
     *
     * @param level
     *            日志级别
     * @return current logMessage
     */
    public LogMessage level(Level level) {
        this.level = level;
        return this;
    }

    public LogMessage currentStack() {
        this.stack = getStack();
        return this;
    }

    public LogMessage currentThread() {
        this.threadName = Thread.currentThread().getName();
        return this;
    }

    public LogMessage stack(String stack) {
        this.stack = stack;
        return this;
    }

    public LogMessage threadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    /**
     * 设置日志格式化工具
     *
     * @param formatter
     *            日志格式化工具
     * @return current logMessage
     */
    public LogMessage formatter(LogFormat formatter) {
        this.formater = formatter;
        return this;
    }

    /**
     * 调用日志log记录本条日志
     */
    public void log() {
        log(level == null ? DEFAULT_LEVEL : level);
    }

    /**
     * 调用日志log记录本条日志
     *
     * @param level
     *            日志级别
     */
    public void log(Level level) {
        this.level = level;

        if (threadName == null) {
            this.threadName = Thread.currentThread().getName();
        }

        if (this.stack == null) {
            this.stack = getStack();
        }

        Log relative = log.relative(LOGMESSAGE_FQNC);
        String msg = formater == null ? this.toString() : formater.format(asInfo());

        switch (level) {
        case TRACE:
            relative.trace(msg);
            break;
        case DEBUG:
            relative.debug(msg);
            break;
        case INFO:
            relative.info(msg);
            break;
        case WARN:
            relative.warn(msg);
            break;
        case ERROR:
            relative.error(msg);
            break;
        }
    }

    public LogInfo asInfo() {
        return new LogInfo() {

            @Override
            public boolean isException() {
                return exception != null;
            }

            @Override
            public Calendar getStartTime() {
                return startTime;
            }

            @Override
            public Object getResult() {
                return result;
            }

            @Override
            public Serializable getOperatorId() {
                return operatorId;
            }

            @Override
            public String getOperator() {
                return operator;
            }

            @Override
            public String getModule() {
                return module;
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public Level getLevel() {
                return level;
            }

            @Override
            public Calendar getFinishTime() {
                return finishTime;
            }

            @Override
            public Throwable getException() {
                return exception;
            }

            @Override
            public String getBizModule() {
                return bizModule;
            }

            @Override
            public Serializable getBizId() {
                return bizId;
            }

            @Override
            public String getAction() {
                return action;
            }

            @Override
            public String getStack() {
                return stack;
            }

            @Override
            public String getThreadName() {
                return threadName;
            }

            @Override
            public long use() {
                return (startTime == null || finishTime == null) ? -1 : finishTime.getTimeInMillis() - startTime.getTimeInMillis();
            }

        };
    }

    @Override
    public String toString() {
        return "{\"bizModule\": \"" + bizModule + "\", \"bizId\": \"" + bizId + "\", \"module\": \"" + module + "\", \"action\": \"" + action
                + "\", \"message\": \"" + message + "\", \"level\": \"" + level + "\", \"operator\": \"" + operator + "\", \"operatorId\": \"" + operatorId
                + "\", \"result\": \"" + result + "\", \"stack\": \"" + stack + "\", \"threadName\": \"" + threadName + "\"}";
    }

}
