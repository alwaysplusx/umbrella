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
package com.harmony.umbrella.monitor.graph;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.monitor.Graph;
import com.harmony.umbrella.util.Formats;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.util.Formats.NullableDateFormat;

/**
 * 监控结果基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractGraph implements Graph {

    protected static final NullableDateFormat ndf = Formats.createDateFormat(Formats.FULL_DATE_PATTERN);

    /**
     * 监控资源的id
     */
    protected final String identifier;

    protected final Map<String, Object> arguments = new LinkedHashMap<String, Object>();

    protected final Map<String, Object> result = new LinkedHashMap<String, Object>();

    protected Calendar requestTime = Calendar.getInstance();

    protected Calendar responseTime;

    protected Exception exception;

    protected String module;

    protected String operator;

    protected Level level;

    public AbstractGraph(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Calendar getRequestTime() {
        return requestTime;
    }

    @Override
    public Calendar getResponseTime() {
        return responseTime;
    }

    @Override
    public boolean isException() {
        return exception != null;
    }

    @Override
    public long use() {
        return (requestTime == null || responseTime == null) ? -1 : responseTime.getTimeInMillis() - requestTime.getTimeInMillis();
    }

    @Override
    public String getJsonResult() {
        return Json.toJson(getResults(), SerializerFeature.WriteMapNullValue);
    }

    @Override
    public String getJsonArguments() {
        return Json.toJson(getArguments(), SerializerFeature.WriteMapNullValue);
    }

    @Override
    public Map<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = Formats.toCalendar(requestTime);
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = Formats.toCalendar(responseTime);
    }

    public void setRequestTime(Calendar requestTime) {
        this.requestTime = requestTime;
    }

    public void setResponseTime(Calendar responseTime) {
        this.responseTime = responseTime;
    }

    @Override
    public Object getArgument(String key) {
        return arguments.get(key);
    }

    @Override
    public void putArgument(String key, Object value) {
        arguments.put(key, value);
    }

    @Override
    public Object getResult(String key) {
        return result.get(key);
    }

    @Override
    public void putResult(String key, Object value) {
        result.put(key, value);
    }

    @Override
    public Map<String, Object> getResults() {
        return result;
    }

    @Override
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n")//
                .append("  id:").append(identifier).append("\n");//
        if (StringUtils.isNotBlank(module)) {
            buffer.append("  module:").append(module).append("\n");
        }
        if (StringUtils.isNotBlank(operator)) {
            buffer.append("  operator:").append(operator).append("\n");
        }
        if (level != null) {
            buffer.append("  level:").append(level).append("\n");
        }
        buffer.append("  requestTime:").append(ndf.format(requestTime)).append("\n")//
                .append("  use:").append(use()).append("\n")//
                .append("  arguments:").append(getJsonArguments()).append("\n")//
                .append("  result:").append(getJsonResult()).append("\n")//
                .append("  exception:").append(isException()).append("\n");
        if (isException()) {
            buffer.append("  exceptionMessage:").append(exception).append("\n");
        }
        return buffer.append("}").toString();
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
