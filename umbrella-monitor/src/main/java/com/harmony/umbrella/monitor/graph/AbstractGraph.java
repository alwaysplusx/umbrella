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

import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.monitor.Graph;
import com.harmony.umbrella.util.Formats;
import com.harmony.umbrella.util.Formats.NullableDateFormat;

/**
 * 监控结果基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractGraph implements Graph {

    /**
     * 拦截方法的返回值
     */
    public static final String METHOD_RESULT = HybridGraph.class.getName() + ".METHOD_RESULT";
    /**
     * 拦截方法的请求参数
     */
    public static final String METHOD_ARGUMENT = HybridGraph.class.getName() + ".METHOD_ARGUMENT";

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
        return Json.toJson(getResult());
    }

    @Override
    public String getJsonArguments() {
        return Json.toJson(getArguments());
    }

    @Override
    public Map<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public Exception getException() {
        return exception;
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

    public void addArgument(String key, Object value) {
        this.arguments.put(key, value);
    }

    public void addAllArgument(Map<String, Object> argument) {
        if (argument != null && !argument.isEmpty()) {
            this.arguments.putAll(argument);
        }
    }

    public void addResult(String key, Object value) {
        this.result.put(key, value);
    }

    public void addAllResult(Map<String, Object> result) {
        if (result != null && !result.isEmpty()) {
            this.result.putAll(result);
        }
    }

    public Object getMethodResult() {
        return result.get(METHOD_RESULT);
    }

    public Object[] getMethodArguments() {
        return (Object[]) this.arguments.get(METHOD_ARGUMENT);
    }

    public void setMethodResult(Object result) {
        this.result.put(METHOD_RESULT, result);
    }

    public void setMethodArguments(Object[] arguments) {
        this.arguments.put(METHOD_ARGUMENT, arguments);
    }

    @Override
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n")//
                .append("  id:").append(identifier).append("\n")//
                .append("  requestTime:").append(ndf.format(requestTime)).append("\n")//
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
