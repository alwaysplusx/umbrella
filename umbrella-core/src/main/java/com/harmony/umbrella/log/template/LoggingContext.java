package com.harmony.umbrella.log.template;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.context.ContextHelper;

/**
 * @author wuxii@foxmail.com
 */
public abstract class LoggingContext {

    protected final ValueContext valueContext = new ValueContext();

    public abstract Object getTarget();

    public abstract Object[] getArguments();

    protected abstract Method getMethod();

    protected abstract Object getResult();

    protected abstract Throwable getException();

    public abstract Object proceed() throws Throwable;

    public HttpSession getHttpSession() {
        return ContextHelper.getHttpSession(false);
    }

    public HttpServletRequest getHttpRequest() {
        return ContextHelper.getHttpRequest();
    }

    public HttpServletResponse getHttpResponse() {
        return ContextHelper.getHttpResponse();
    }

    public ValueContext getValueContext() {
        return valueContext;
    }

    public class ValueContext {

        protected final Map<String, Object> inContext = new HashMap<String, Object>();
        protected final Map<String, Object> outContext = new HashMap<String, Object>();

        private ValueContext() {
        }

        public Map<String, Object> getInContext() {
            return inContext;
        }

        public Map<String, Object> getOutContext() {
            return outContext;
        }

    }

}
