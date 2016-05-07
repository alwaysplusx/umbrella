package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.monitor.AbstractMonitor;
import com.harmony.umbrella.monitor.GraphListener;
import com.harmony.umbrella.core.MethodGraph;
import com.harmony.umbrella.monitor.MethodMonitor;
import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.monitor.annotation.Mode;
import com.harmony.umbrella.monitor.graph.AbstractGraph;
import com.harmony.umbrella.monitor.matcher.MethodExpressionMatcher;

/**
 * 基于拦截器的实现，默认{@link MethodMonitor#DEFAULT_METHOD_PATTERN} 表达式的方法.
 * <p/>
 * 当然要连接器能拦截到方法
 *
 * @param <IC>
 *            方法执行的上下文， 如JDK动态代理的
 *            <p/>
 *            {@linkplain java.lang.reflect.InvocationHandler}中的参数一样组成的反射上下文
 *            <p/>
 *            {@linkplain javax.interceptor.InvocationContext}
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMethodMonitorInterceptor<IC> extends AbstractMonitor<Method> implements MethodMonitor {

    protected List<GraphListener<MethodGraph>> graphListeners = new ArrayList<GraphListener<MethodGraph>>();

    private boolean raiseError = false;

    /**
     * 资源模版匹配工具
     */
    private ResourceMatcher<Method> resourceMatcher;

    @Override
    public ResourceMatcher<Method> getResourceMatcher() {
        if (resourceMatcher == null) {
            resourceMatcher = new MethodExpressionMatcher();
        }
        return resourceMatcher;
    }

    /**
     * 监控的方法
     *
     * @param ctx
     * @see {@linkplain javax.interceptor.InvocationContext#getMethod()}
     */
    protected abstract Method getMethod(IC ctx);

    /**
     * 执行监控的目标方法
     *
     * @param ctx
     *            执行的上下文
     * @see {@linkplain javax.interceptor.InvocationContext#proceed()}
     */
    protected abstract Object process(IC ctx) throws Exception;

    /**
     * 上层入口， 监控的适配方法
     */
    protected Object monitor(IC ctx) throws Exception {
        Method method = getMethod(ctx);
        try {
            if (method != null && isMonitored(method)) {
                return aroundMonitor(method, ctx);
            }
            return process(ctx);
        } catch (Exception e) {
            if (raiseError) {
                throw e;
            }
            LOG.info("monitor received a exception, {}", e.toString());
            return null;
        }
    }

    /**
     * 环绕Method执行监控
     *
     * @param method
     *            监控的方法， 唯一键
     * @param ctx
     *            执行的上下文
     * @return 执行返回的结果
     */
    protected abstract Object aroundMonitor(Method method, IC ctx) throws Exception;

    /**
     * 通过方法的信息获取监控对象的内部信息，并设置在graph上
     *
     * @param graph
     *            监控结果视图
     * @param target
     *            监控对象
     * @param method
     *            监控的方法
     */
    protected void applyMethodRequestProperty(AbstractGraph graph, Object target, Method method) {
        Map<String, Object> property = new HashMap<String, Object>();
        property.putAll(attackMethodProperty(target, method, Mode.IN));
        if (!property.isEmpty()) {
            graph.putArgument(MethodGraph.METHOD_PROPERTY, property);
        }
    }

    /**
     * 通过方法信息获取监控对象的内部信息，并设置在graph上
     *
     * @param graph
     *            监控结果视图
     * @param target
     *            监控对象
     * @param method
     *            监控的方法
     */
    protected void applyMethodResponseProperty(AbstractGraph graph, Object target, Method method) {
        Map<String, Object> property = new HashMap<String, Object>();
        property.putAll(attackMethodProperty(target, method, Mode.OUT));
        if (!property.isEmpty()) {
            graph.putResult(MethodGraph.METHOD_PROPERTY, property);
        }
    }

    protected void notifyGraphListeners(MethodGraph graph) {
        for (GraphListener<MethodGraph> listener : graphListeners) {
            listener.analyze(graph);
        }
    }

    @Override
    public void cleanAll() {
        this.graphListeners.clear();
        super.cleanAll();
    }

    @Override
    public void destroy() {
        this.cleanAll();
    }

    public boolean isRaiseError() {
        return raiseError;
    }

    public void setRaiseError(boolean raiseError) {
        this.raiseError = raiseError;
    }

    public void addGraphListener(GraphListener<MethodGraph> graphListener) {
        this.graphListeners.add(graphListener);
    }

    public void setGraphListeners(List<GraphListener<MethodGraph>> graphListeners) {
        this.graphListeners = graphListeners;
    }

}
