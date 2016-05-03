package com.harmony.umbrella.log.template;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author wuxii@foxmail.com
 */
public class Holder extends ArrayList<Object> {

    private static final long serialVersionUID = 1L;
    final Object target;
    final Object result;
    final Object[] arguments;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public Holder(Object target, Object result, Object[] arguments) {
        super(Arrays.asList(arguments));
        this.target = target;
        this.result = result;
        this.arguments = arguments;
    }

    public Holder(Object target, Object result, Object[] arguments, HttpServletRequest request, HttpServletResponse response) {
        super(Arrays.asList(arguments));
        this.target = target;
        this.result = result;
        this.arguments = arguments;
        this.request = request;
        this.response = response;
    }

    public Object get$() {
        return target;
    }

    public Object getTarget() {
        return target;
    }

    public Object getResult() {
        return result;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Object[] getArgs() {
        return arguments;
    }

    public Object[] getArg() {
        return arguments;
    }

    public String getIp() {
        return request.getRemoteAddr();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ServletContext getApplication() {
        return request.getServletContext();
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

}
