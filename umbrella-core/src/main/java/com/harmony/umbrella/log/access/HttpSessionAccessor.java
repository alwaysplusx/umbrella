package com.harmony.umbrella.log.access;

import java.lang.reflect.Method;

import javax.servlet.http.HttpSession;

import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HttpSessionAccessor extends CheckedAccessor<HttpSession> {

    public HttpSessionAccessor() {
        super(HttpSession.class);
    }

    @Override
    public boolean support(String name) {
        return StringUtils.isNotBlank(name);
    }

    @Override
    public Object get(String name, HttpSession obj) {
        if (name.startsWith("#")) {
            Method method = ReflectionUtils.findReadMethod(getType(), name);
            if (method == null) {
                throw new IllegalArgumentException(name + " method not found");
            }
            return ReflectionUtils.invokeMethod(method, obj);
        }
        return obj.getAttribute(name.startsWith("\\") ? name.substring(1) : name);
    }

    @Override
    public void set(String name, HttpSession obj, Object val) {
        obj.setAttribute(name, val);
    }

}
