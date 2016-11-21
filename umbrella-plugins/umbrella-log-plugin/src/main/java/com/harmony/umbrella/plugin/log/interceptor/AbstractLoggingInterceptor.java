package com.harmony.umbrella.plugin.log.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.interceptor.InvocationContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Module;
import com.harmony.umbrella.plugin.log.expression.LoggingTemplateFactory;
import com.harmony.umbrella.plugin.log.expression.LoggingTemplateFactory.LoggingTemplate;
import com.harmony.umbrella.plugin.log.expression.Property;
import com.harmony.umbrella.plugin.log.expression.ValueContext;
import com.harmony.umbrella.plugin.log.expression.ValueStack;
import com.harmony.umbrella.plugin.log.util.ObjectSerializerFactory;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractLoggingInterceptor {

    protected LoggingTemplateFactory loggingTemplateFactory = new LoggingTemplateFactory();

    protected Set<String> ignoreHttpAttributes = new HashSet<String>();

    protected ObjectSerializer serializer = ObjectSerializerFactory.createDefault();

    protected abstract ValueContext createValueContext();

    protected void logging(LogMessage logMessage) {
        logMessage.log();
    }

    public Object aroundLogging(InvocationContext invocation) throws Exception {
        Method method = invocation.getMethod();
        if (method == null) {
            return invocation.proceed();
        }

        Object result = null;
        Throwable exception = null;
        Class<?> targetClass = invocation.getTarget().getClass();

        LogMessage logMessage = LogMessage.create(Logs.getLog(targetClass)).stack(StringUtils.getMethodId(method)).currentThread();

        final Logging loggingAnn = method.getAnnotation(Logging.class);
        final boolean hasLoggingAnnotation = loggingAnn != null;

        /*
         * apply user context to logMessage
         * login method set user context after method proceed
         * logout remove it after method proceed
         */
        boolean isUserContextSet = applyUserContext(logMessage);

        final ValueContext valueContext = createValueContext();
        final Map<String, Object> inContext = valueContext.getInContext();
        final Map<String, Object> outContext = valueContext.getOutContext();

        ValueStack.push(valueContext);
        try {
            if (hasLoggingAnnotation) {
                // 填充in context内容
                fillInContext(inContext, invocation);
            }

            logMessage.start();

            // invocation proceed
            result = invocation.proceed();

            logMessage.finish();//
            logMessage.result(result);

        } catch (Throwable e) {
            exception = e;
            logMessage.exception(e);
            logMessage.level(Level.ERROR);
        } finally {
            ValueStack.pop();
        }

        if (hasLoggingAnnotation) {
            // 填充out context内容
            fillOutContext(outContext, invocation, result, exception);
        }

        if (!isUserContextSet) {
            applyUserContext(logMessage);
        }

        // do logging
        if (hasLoggingAnnotation && StringUtils.isNotBlank(loggingAnn.module())) {
            // @Logging配置优先
            logMessage.module(loggingAnn.module());
        } else {
            // apply module annotation feature
            Module moduleAnn = targetClass.getAnnotation(Module.class);
            if (moduleAnn != null) {
                logMessage.module(moduleAnn.value());
            }
        }

        try {
            // apply logging annotation feature
            if (hasLoggingAnnotation) {
                LoggingTemplate template = loggingTemplateFactory.getLoggingTemplate(loggingAnn);
                logMessage.action(loggingAnn.action())//
                        .level(loggingAnn.level())//
                        .key(template.getId(valueContext))//
                        .message(template.getMessage(valueContext));
                // set log context property
                Property[] properties = template.getProperties();
                for (Property p : properties) {
                    logMessage.put(p.getName(), valueContext.find(p.getExpression()));
                }

            } else {
                logMessage.level(Level.DEBUG)//
                        .message("execute method {}, result is {}", StringUtils.getMethodId(method), result);//
            }

            logging(logMessage);
        } catch (Exception e) {
            // ignore this exception
            e.printStackTrace();
        }

        // if proceed throw exception throw it out
        if (exception != null) {
            ReflectionUtils.rethrowException(exception);
        }

        return result;
    }

    protected void fillInContext(Map<String, Object> inContext, InvocationContext invocation) {
        // http context
        applyHttpContext(inContext);
        // invocation context
        applyInvocationContext(inContext, invocation);
    }

    protected void fillOutContext(Map<String, Object> outContext, InvocationContext invocation, Object result, Throwable ex) {
        // http context
        applyHttpContext(outContext);

        // invocation context
        applyInvocationContext(outContext, invocation);

        outContext.put("result", result);
        outContext.put("exception", ex);
    }

    protected boolean applyUserContext(LogMessage logMessage) {
        CurrentContext cc = getCurrentContext();
        if (cc != null && cc.getUserId() != null) {
            logMessage.operatorId(cc.getUserId())//
                    .operatorName(cc.getUsername())//
                    .operatorHost(cc.getUserHost());
            return true;
        }
        return false;
    }

    private void applyInvocationContext(Map<String, Object> context, InvocationContext invocation) {
        // target
        // WUXII 使用JSON序列化对象, 使得对象不随方法的运行而内部值遭到更改
        Object target = serializer.serialize(invocation.getTarget());
        context.put("target", target);
        context.put("$", target);

        // argument
        List<Object> arguments = new ArrayList<Object>();
        Object[] args = invocation.getParameters();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object argument = serializer.serialize(args[i]);
                context.put(i + "", argument);
                context.put("args[" + i + "]", argument);
                arguments.add(argument);
            }
        }
        context.put("args", arguments);
    }

    protected void applyHttpContext(Map<String, Object> context) {
        context.putAll(getHttpContextSnapshot(getRequest(), getResponse()));
    }

    protected final Map<String, Object> getHttpContextSnapshot(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (request != null) {

            {
                // method attribute
                result.put("ip", request.getRemoteAddr());
            }

            // request header map
            {
                HashMap<String, Object> headerMap = new HashMap<String, Object>();
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    headerMap.put(name, request.getHeader(name));
                }
                result.put("header", headerMap);
            }

            // request parameter
            {
                HashMap<String, Object> parameterMap = new HashMap<String, Object>();
                Enumeration<String> parameterNames = request.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    String name = parameterNames.nextElement();
                    parameterMap.put(name, request.getParameter(name));
                }
                result.put("parameter", parameterMap);
            }

            // request attribute map
            {
                HashMap<String, Object> attributeMap = new HashMap<String, Object>();
                Enumeration<String> attributeNames = request.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    String name = attributeNames.nextElement();
                    attributeMap.put(name, request.getAttribute(name));
                }
                result.put("request", attributeMap);
            }

            // session map
            {
                HashMap<String, Object> sessionMap = new HashMap<String, Object>();
                HttpSession session = request.getSession(false);
                if (session != null) {
                    Enumeration<String> sessionAttrNames = session.getAttributeNames();
                    while (sessionAttrNames.hasMoreElements()) {
                        String name = sessionAttrNames.nextElement();
                        if (!isIgnoreHttpAttribute(name)) {
                            sessionMap.put(name, session.getAttribute(name));
                        }
                    }
                }
                result.put("session", sessionMap);
            }

            // application map
            {
                HashMap<String, Object> applicationMap = new HashMap<String, Object>();
                ServletContext servletContext = request.getServletContext();
                Enumeration<String> applicationNames = servletContext.getAttributeNames();
                while (applicationNames.hasMoreElements()) {
                    String name = applicationNames.nextElement();
                    if (!isIgnoreHttpAttribute(name)) {
                        applicationMap.put(name, servletContext.getAttribute(name));
                    }
                }
                result.put("application", applicationMap);
            }
        }

        if (response != null) {
            HashMap<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put("status", response.getStatus());
            result.put("response", responseMap);
        }

        return result;
    }

    protected boolean isIgnoreHttpAttribute(String key) {
        if (key == null) {
            return true;
        }
        for (String att : ignoreHttpAttributes) {
            if (key.startsWith(att)) {
                return true;
            }
        }
        return false;
    }

    protected CurrentContext getCurrentContext() {
        return ContextHelper.getCurrentContext();
    }

    protected HttpServletRequest getRequest() {
        return ContextHelper.getHttpRequest();
    }

    protected HttpServletResponse getResponse() {
        return ContextHelper.getHttpResponse();
    }

    public void addIgnoreHttpAttributes(String... attrs) {
        Collections.addAll(ignoreHttpAttributes, attrs);
    }

    public Set<String> getIgnoreHttpAttributes() {
        return ignoreHttpAttributes;
    }

    public void setIgnoreHttpAttributes(Set<String> ignoreHttpAttributes) {
        this.ignoreHttpAttributes = ignoreHttpAttributes;
    }

    public LoggingTemplateFactory getLoggingTemplateFactory() {
        return loggingTemplateFactory;
    }

    public void setLoggingTemplateFactory(LoggingTemplateFactory loggingTemplateFactory) {
        this.loggingTemplateFactory = loggingTemplateFactory;
    }

    public ObjectSerializer getObjectSerializer() {
        return serializer;
    }

    public void setObjectSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    public interface ObjectSerializer {

        Object serialize(Object val);

    }

}
