package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.harmony.umbrella.monitor.HttpAttacker;
import com.harmony.umbrella.monitor.annotation.Monitor;
import com.harmony.umbrella.monitor.attack.SimpleHttpAttacker;
import com.harmony.umbrella.monitor.graph.HybridGraph;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author wuxii@foxmail.com
 */
public class StrutsMonitorInterceptor extends AbstractHttpMonitor<ActionInvocation> implements Interceptor {

    private static final long serialVersionUID = -6402962474792262484L;

    private String urlPatterns;
    private boolean raiseError;

    private HttpAttacker httpAttacker = new SimpleHttpAttacker();

    @Override
    public void init() {
        StringTokenizer st = new StringTokenizer(urlPatterns == null ? DEFAULT_PATH_PATTERN : urlPatterns, ",|");
        while (st.hasMoreTokens()) {
            patternList.add(st.nextToken().trim());
        }
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        return (String) monitor(ServletActionContext.getRequest(), ServletActionContext.getResponse(), invocation);
    }

    @Override
    protected Object aroundMonitor(String resourceId, HttpServletRequest request, HttpServletResponse response, ActionInvocation nexus) throws Exception {
        Method method = getMethod(nexus);
        if (method == null) {
            return process(request, response, nexus);
        }

        HybridGraph graph = new HybridGraph(resourceId, nexus.getAction());
        graph.setMethod(method);

        Monitor ann = method.getAnnotation(Monitor.class);
        if (ann != null) {
            applyMonitorInformation(graph, ann);
        }

        applyHttpRequestFeature(graph, request);
        applyHttpRequestProperty(graph, request, method);
        applyMethodRequestProperty(graph, nexus.getAction(), method);

        Object result = null;
        try {

            result = process(request, response, nexus);

            graph.setResponseTime(Calendar.getInstance());
            graph.setMethodResult(result);

            applyHttpResponseFeature(graph, response);
            applyHttpResponseProperty(graph, request, method);
            applyMethodResponseProperty(graph, nexus.getAction(), method);

        } catch (Exception e) {
            graph.setException(e);
            if (raiseError) {
                throw e;
            }
        } finally {
            notifyGraphListeners(graph);
        }
        return result;
    }

    @Override
    protected Object process(HttpServletRequest request, HttpServletResponse response, ActionInvocation nexus) throws Exception {
        return nexus.invoke();
    }

    protected Method getMethod(ActionInvocation ctx) {
        ActionProxy proxy = ctx.getProxy();
        String methodName = proxy.getMethod();
        if (methodName == null) {
            methodName = proxy.getConfig().getMethodName();
        }
        try {
            return ctx.getAction().getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Override
    protected HttpAttacker getHttpAttacker() {
        return this.httpAttacker;
    }

    public boolean isRaiseError() {
        return raiseError;
    }

    public void setRaiseError(boolean raiseError) {
        this.raiseError = raiseError;
    }

    public String getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

}
