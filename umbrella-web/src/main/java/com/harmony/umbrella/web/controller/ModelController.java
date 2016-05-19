package com.harmony.umbrella.web.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.web.render.HttpRender;
import com.harmony.umbrella.web.render.WebHttpRender;

/**
 * @author wuxii@foxmail.com
 */
public class ModelController {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;

    private HttpRender render = new WebHttpRender();

    protected String success() {
        return success(null);
    }

    protected String success(String message) {
        return renderJson(true, message);
    }

    protected String error() {
        return error(null);
    }

    protected String error(String message) {
        return renderJson(false, message);
    }

    protected String renderJson(boolean success, String message) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", success);
        if (message != null) {
            result.put("message", message);
        }
        return Json.toJson(result);
    }

    protected String renderJson(Object obj, String... excludes) {
        return null;
    }

    public void setRender(HttpRender render) {
        this.render = render;
    }

    public HttpRender getRender() {
        return render;
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        return response;
    }
}
