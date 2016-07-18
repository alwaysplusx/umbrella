package com.harmony.umbrella.log.support;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Module;
import com.harmony.umbrella.log.expression.LoggingTemplate;
import com.harmony.umbrella.log.expression.ValueContext;
import com.harmony.umbrella.log.expression.ValueStack;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class LoggingInterceptor {

    protected void logging(LogMessage logMessage) {
        logMessage.log();
    }

    public Object logging(InvocationContext invocation) throws Exception {

        Object result = null;
        Exception ex = null;

        Method method = invocation.getMethod();
        Object target = invocation.getTarget();

        Class<?> targetClass = (target == null) ? method.getDeclaringClass() : target.getClass();

        LogMessage logMessage = LogMessage.create(Logs.getLog(targetClass))//
                .stack(StringUtils.getMethodId(method))//
                .threadName(Thread.currentThread().getName());//

        Module moduleAnn = targetClass.getAnnotation(Module.class);
        if (moduleAnn != null) {
            logMessage.module(moduleAnn.value());
        }

        Logging loggingAnn = method.getAnnotation(Logging.class);
        if (loggingAnn != null) {
            //如果不为空覆盖原来的记录
            if (StringUtils.isNotBlank(loggingAnn.module())) {
                logMessage.module(loggingAnn.module());
            }
            logMessage.action(loggingAnn.action())//
                    .type(loggingAnn.type())//
                    .level(loggingAnn.level());
        }

        final ValueContext valueContext = createValueContext();
        final Map<String, Object> inContext = valueContext.getInContext();
        final Map<String, Object> outContext = valueContext.getOutContext();

        ValueStack.push(valueContext);

        boolean userContextAlreadyBeenSet = applyUserContext(logMessage);

        // 开始请求的request/response快照
        inContext.putAll(snapshot(getRequest(), getResponse()));
        // 填充in context内容
        fillInContext(inContext, invocation);

        try {
            logMessage.start();

            // invoke invocation context
            result = invocation.proceed();

            logMessage.finish();//
            logMessage.result(result);

            logMessage.result(result);

        } catch (Exception e) {
            logMessage.exception(ex = e);
            logMessage.level(Level.ERROR);
        } finally {
            ValueStack.pop();
        }

        // 方法相应后的 request/response快照
        outContext.putAll(snapshot(getRequest(), getResponse()));
        // 填充out context内容
        fillOutContext(outContext, result, ex);

        if (!userContextAlreadyBeenSet) {
            // if login method set user context after log success
            applyUserContext(logMessage);
        }

        if (loggingAnn != null) {
            LoggingTemplate template = new LoggingTemplate(loggingAnn);
            logMessage.key(template.getId(valueContext))//
                    .message(template.getMessage(valueContext));
        } else {
            logMessage.message("execute method {}, result is {}", StringUtils.getMethodId(method), result);
        }

        try {
            logging(logMessage);
        } catch (Exception e) {
            // ignore this exception
            e.printStackTrace();
        }

        // if proceed throw exception throw out
        if (ex != null) {
            throw ex;
        }

        return result;
    }

    protected void fillInContext(Map<String, Object> inContext, InvocationContext invocation) {
        inContext.put("target", invocation.getTarget());
        inContext.put("$", invocation.getTarget());
        Object[] args = invocation.getParameters();
        inContext.put("args", args);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                inContext.put(i + "", args[i]);
            }
        }
        HttpServletRequest request = getRequest();
        if (request != null) {
            inContext.put("request", request);
            inContext.put("ip", request.getRemoteAddr());
        }
    }

    protected void fillOutContext(Map<String, Object> outContext, Object result, Exception ex) {
        outContext.put("result", result);
        outContext.put("exception", ex);
        HttpServletResponse response = getResponse();
        if (response != null) {
            outContext.put("response", response);
        }
    }

    protected Map<String, Object> snapshot(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (request != null) {

            // request header map
            HashMap<String, Object> headerMap = new HashMap<String, Object>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headerMap.put(name, request.getHeader(name));
            }
            result.put("header", headerMap);

            // request parameter
            HashMap<String, Object> parameterMap = new HashMap<String, Object>();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                parameterMap.put(name, request.getParameter(name));
            }
            result.put("parameter", parameterMap);

            // request attribute map
            HashMap<String, Object> attributeMap = new HashMap<String, Object>();
            Enumeration<String> attributeNames = request.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                attributeMap.put(name, request.getAttribute(name));
            }
            result.put("attribute", attributeMap);

            // session map
            HashMap<String, Object> sessionMap = new HashMap<String, Object>();
            HttpSession session = request.getSession(false);
            if (session != null) {
                Enumeration<String> sessionAttrNames = session.getAttributeNames();
                while (sessionAttrNames.hasMoreElements()) {
                    String name = sessionAttrNames.nextElement();
                    attributeMap.put(name, session.getAttribute(name));
                }
            }
            result.put("session", sessionMap);

            // application map
            HashMap<String, Object> applicationMap = new HashMap<String, Object>();
            ServletContext servletContext = request.getServletContext();
            Enumeration<String> applicationNames = servletContext.getAttributeNames();
            while (applicationNames.hasMoreElements()) {
                String name = applicationNames.nextElement();
                applicationMap.put(name, session.getAttribute(name));
            }
            result.put("application", applicationMap);
        }

        if (response != null) {
            HashMap<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put("status", response.getStatus());
        }

        return result;
    }

    protected boolean applyUserContext(LogMessage logMessage) {
        CurrentContext cc = ApplicationContext.getCurrentContext();
        if (cc != null && cc.getUserId() != null) {
            logMessage.operatorId(cc.getUserId())//
                    .operatorName(cc.getUsername())//
                    .operatorHost(cc.getUserHost());
            return true;
        }
        return false;
    }

    protected abstract ValueContext createValueContext();

    protected HttpServletRequest getRequest() {
        return ContextHelper.getHttpRequest();
    }

    protected HttpServletResponse getResponse() {
        return ContextHelper.getHttpResponse();
    }

}
