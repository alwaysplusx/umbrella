package com.harmony.umbrella.monitor.graph;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.monitor.Graph;
import com.harmony.umbrella.monitor.Monitor.MonitorPolicy;
import com.harmony.umbrella.util.Formats;
import com.harmony.umbrella.util.Formats.NullableDateFormat;

/**
 * 监控结果基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractGraph implements Graph, Serializable {

    private static final long serialVersionUID = 4915338905076913082L;

    protected static final NullableDateFormat ndf = Formats.createDateFormat(Formats.FULL_DATE_PATTERN);

    /**
     * 监控资源的id
     */
    protected final String identifier;

    protected final Map<String, Object> arguments = new LinkedHashMap<String, Object>();

    protected final Map<String, Object> result = new LinkedHashMap<String, Object>();

    protected String monitorName;

    protected MonitorPolicy policy;

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
    public String getMonitorName() {
        return monitorName;
    }

    @Override
    public MonitorPolicy getPolicy() {
        return policy;
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
        buffer.append("{\n");
        buffer.append("  id:").append(identifier).append("\n");//
        buffer.append("  requestTime:").append(ndf.format(requestTime)).append("\n");//
        buffer.append("  use:").append(use()).append("\n");//
        buffer.append("  arguments:").append(getJsonArguments()).append("\n");//
        buffer.append("  result:").append(getJsonResult()).append("\n");//
        buffer.append("  exception:").append(isException()).append("\n");

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
