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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.harmony.umbrella.monitor.Graph;
import com.harmony.umbrella.util.Formats;
import com.harmony.umbrella.util.Formats.NullableDateFormat;

/**
 * 监视结果基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractGraph implements Graph {

    protected static final NullableDateFormat ndf = Formats.createDateFormat(Formats.FULL_DATE_PATTERN);

    /**
     * 监控资源的id
     */
    protected final String identifier;

    protected final Map<String, Object> arguments = new HashMap<String, Object>();
    
    protected Object result;

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
        if (requestTime != null && responseTime != null) {
            return responseTime.getTimeInMillis() - requestTime.getTimeInMillis();
        }
        return -1;
    }
    
    @Override
    public Map<String, Object> getArguments() {
        return Collections.unmodifiableMap(arguments);
    }
    
    @Override
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setRequestTime(Calendar requestTime) {
        this.requestTime = requestTime;
    }

    public void setResponseTime(Calendar responseTime) {
        this.responseTime = responseTime;
    }
    
    @Override
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n")
            .append("  id:").append(identifier).append("\n")
            .append("  requestTime:").append(ndf.format(requestTime)).append("\n")
            .append("  use:").append(use()).append("\n")
            .append("  arguments:").append(formatObjectValue(getArguments())).append("\n")
            .append("  result:").append(formatObjectValue(getResult())).append("\n")
            .append("  exception:").append(isException()).append("\n");
        if (isException()) {
            buffer.append("  exceptionMessage:").append(exception).append("\n");
        }
        return buffer.append("}").toString();
    }
    
    @SuppressWarnings("rawtypes")
    protected String formatObjectValue(Object value) {
        if (value instanceof Map) {
            return formatMap((Map) value);
        }
        return String.valueOf(value);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected String formatMap(Map obj) {
        if (obj == null)
            return "";
        StringBuilder buffer = new StringBuilder("{");
        Iterator<Entry> iterator = obj.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            buffer.append(entry.getKey()).append(":").append(formatObjectValue(entry.getValue()));
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        return buffer.append("}").toString();
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
