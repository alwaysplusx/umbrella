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

    private Log log;

    private Serializable id;
    private String entityClassName;
    private String message;
    private String module;
    private String action;
    private Throwable exception;

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

    public LogMessage message(String message, Object... args) {
        this.message = message;
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

    public void log() {
        log.info(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{id:");
        builder.append(id);
        builder.append(", entityClassName:");
        builder.append(entityClassName);
        builder.append(", message:");
        builder.append(message);
        builder.append(", module:");
        builder.append(module);
        builder.append(", action:");
        builder.append(action);
        builder.append(", exception:");
        builder.append(exception);
        builder.append("}");
        return builder.toString();
    }

}
