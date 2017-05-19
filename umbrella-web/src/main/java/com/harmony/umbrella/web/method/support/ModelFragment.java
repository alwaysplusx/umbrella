package com.harmony.umbrella.web.method.support;

import static com.harmony.umbrella.web.method.support.ModelFragment.Scope.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.TypeConverter;

/**
 * @author wuxii@foxmail.com
 */
public class ModelFragment {

    public static final String MODEL_FRAGMENT = ModelFragment.class.getName() + ".modelFragment";

    protected TypeConverter converter;

    protected Map<String, Object> submitData = new LinkedHashMap<>();

    protected Map<String, Object> requestAttribute = new LinkedHashMap<>();
    protected Map<String, Object> sessionAttribute = new LinkedHashMap<>();
    protected Map<String, Object> applicationAttribute = new LinkedHashMap<>();

    protected ModelFragment(TypeConverter converter) {
        this.converter = converter;
    }

    public ModelFragment addAttribute(String name, Object value) {
        return addRequestAttribute(name, value);
    }

    public ModelFragment addRequestAttribute(String name, Object value) {
        addAttribute(REQUEST, name, value);
        return this;
    }

    public ModelFragment addSessionAttribute(String name, Object value) {
        addAttribute(SESSION, name, value);
        return this;
    }

    public ModelFragment addApplicationAttribute(String name, Object value) {
        addAttribute(APPLICATION, name, value);
        return this;
    }

    public void addAttribute(Scope scope, String name, Object value) {
        get(scope).put(name, value);
    }

    public Object getAttribute(String name) {
        return getRequestAttribute(name);
    }

    public Object getRequestAttribute(String name) {
        return getAttribute(REQUEST, name);
    }

    public Object getSessionAttribute(String name) {
        return getAttribute(SESSION, name);
    }

    public Object getApplicationAttribute(String name) {
        return getAttribute(APPLICATION, name);
    }

    public Object getAttribute(Scope scope, String name) {
        return get(scope).get(name);
    }

    public void removeAttribute(Scope scope, String name) {
        get(scope).remove(name);
    }

    public Object getData(String key) {
        return submitData.get(key);
    }

    public <T> T getData(String key, Class<T> type) {
        return converter.convertIfNecessary(getData(key), type);
    }

    public Set<String> getDataKeys() {
        return submitData.keySet();
    }

    public Set<String> getKeys(Scope scope) {
        return get(scope).keySet();
    }

    public void render(HttpServletRequest request) throws IOException, ServletException {

        Iterator<Entry<String, Object>> it = requestAttribute.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                request.removeAttribute(key);
            } else {
                request.setAttribute(key, value);
            }
        }

        it = sessionAttribute.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                request.getSession().removeAttribute(key);
            } else {
                request.getSession().setAttribute(key, value);
            }
        }

        it = applicationAttribute.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                request.getServletContext().removeAttribute(key);
            } else {
                request.getServletContext().setAttribute(key, value);
            }
        }
    }

    protected Map get(Scope scope) {
        switch (scope) {
        case APPLICATION:
            return applicationAttribute;
        case SESSION:
            return sessionAttribute;
        default:
            return requestAttribute;
        }
    }

    protected void setData(String key, Object value) {
        submitData.put(key, value);
    }

    public static enum Scope {
        SESSION, REQUEST, APPLICATION
    }

}