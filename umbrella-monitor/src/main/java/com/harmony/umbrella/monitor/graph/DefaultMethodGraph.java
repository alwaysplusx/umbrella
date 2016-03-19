package com.harmony.umbrella.monitor.graph;

import java.lang.reflect.Method;

import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.Monitor.MonitorPolicy;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultMethodGraph implements MethodGraph {

    protected final Method method;
    protected final String graphId;

    protected Object target;
    protected Object result;
    protected Object[] parameters;

    protected MonitorPolicy policy;
    protected long requestTime = -1;
    protected long responseTime = -1;
    protected Throwable throwable;

    public DefaultMethodGraph(Method method) {
        this.method = method;
        this.graphId = StringUtils.getMethodId(method);
    }

    public DefaultMethodGraph(Object target, Method method, Object[] parameters) {
        this.method = method;
        this.graphId = StringUtils.getMethodId(method);
        this.parameters = parameters;
        this.target = target;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Class<?> getTargetClass() {
        return target.getClass();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public String getGraphType() {
        return GRAPH_TYPE;
    }

    @Override
    public String getGraphId() {
        return graphId;
    }

    @Override
    public MonitorPolicy getPolicy() {
        return policy;
    }

    @Override
    public long getRequestTime() {
        return requestTime;
    }

    @Override
    public long getResponseTime() {
        return responseTime;
    }

    @Override
    public long use() {
        return requestTime > 0 && responseTime > 0 ? responseTime - requestTime : -1;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public boolean isThrowable() {
        return throwable != null;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public void setPolicy(MonitorPolicy policy) {
        this.policy = policy;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
    
    @Override
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n");
        buffer.append("  graphId:").append(graphId).append("\n");//
        buffer.append("  requestTime:").append(requestTime).append("\n");//
        buffer.append("  use:").append(use()).append("\n");//
        buffer.append("  parameters:").append(parameters).append("\n");//
        buffer.append("  result:").append(result).append("\n");//
        buffer.append("  throwable:").append(isThrowable()).append("\n");
        if (isThrowable()) {
            buffer.append("  exceptionMessage:").append(throwable).append("\n");
        }
        return buffer.append("}").toString();
    }
    @Override
    public String toString() {
        return getDescription();
    }
}