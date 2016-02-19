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

/**
 * @author wuxii@foxmail.com
 */
public class LogMessage {

    public static final String FQNC = LogMessage.class.getName();

    private Log log;

    /**
     * 业务数据的唯一id
     */
    private Serializable id;
    /**
     * 业务数据的实体类名
     */
    private String entityClassName;
    /**
     * 日志消息
     */
    private String message;
    /**
     * 日志消息所属的模块
     */
    private String module;
    /**
     * 记录的动作
     */
    private String action;
    /**
     * 记录的异常
     */
    private Throwable exception;
    /**
     * 日志级别
     */
    private Level level;
    /**
     * 操作员
     */
    private String operator;
    /**
     * 操作员id
     */
    private Serializable operatorId;

    private LogMessageFormat formater;

    public LogMessage(Log log) {
        this.log = log;
    }

    public static LogMessage create(Log log) {
        return new LogMessage(log);
    }

    public LogMessage id(Serializable id) {
        this.id = id;
        return this;
    }

    public LogMessage entityClass(Class<?> entityClass) {
        this.entityClassName = entityClass.getName();
        return this;
    }

    public LogMessage entityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
        return this;
    }

    public LogMessage message(String message) {
        return message(message, new Object[0]);
    }

    /**
     * 带格式话的消息模式
     * 
     * @param message
     *            消息模版
     * @param args
     *            消息参数
     * @return
     */
    public LogMessage message(String message, Object... args) {
        if (message != null) {
            this.message = message;
        }
        if (args[args.length - 1] instanceof Throwable) {
            this.exception = (Throwable) args[args.length - 1];
        }
        return this;
    }

    public LogMessage module(String module) {
        this.module = module;
        return this;
    }

    public LogMessage action(String action) {
        this.action = action;
        return this;
    }

    public LogMessage exception(Throwable exception) {
        this.exception = exception;
        return this;
    }

    public LogMessage operator(String username) {
        this.operator = username;
        return this;
    }

    public LogMessage operatorId(Serializable operatorId) {
        this.operatorId = operatorId;
        return this;
    }

    public LogMessage level(Level level) {
        this.level = level;
        return this;
    }

    /**
     * 设置日志格式化工具
     * 
     * @param formatter
     * @return
     */
    public LogMessage formatter(LogMessageFormat formatter) {
        this.formater = formatter;
        return this;
    }

    public void log() {
        log(level == null ? Level.INFO : level);
    }

    public void log(Level level) {
        Log relative = log.relative(FQNC);
        this.level = level;
        String msg = formater == null ? this.toString() : formater.format(this);
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

    public Serializable getId() {
        return id;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public String getMessage() {
        return message;
    }

    public String getModule() {
        return module;
    }

    public String getAction() {
        return action;
    }

    public Throwable getException() {
        return exception;
    }

    public Level getLevel() {
        return level;
    }

    public String getOperator() {
        return operator;
    }

    public Serializable getOperatorId() {
        return operatorId;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(operator).append("[").append(operatorId).append("]")//
                .append("：").append(action).append("[").append(module).append("]").append("的")//
                .append(entityClassName).append("[").append(id).append("]").append(message).toString();
    }
}
