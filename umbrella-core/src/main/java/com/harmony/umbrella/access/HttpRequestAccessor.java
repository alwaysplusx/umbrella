package com.harmony.umbrella.access;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.access.CheckedAccessor;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HttpRequestAccessor extends CheckedAccessor<HttpServletRequest> {

    public HttpRequestAccessor() {
        super(HttpServletRequest.class);
    }

    @Override
    public boolean support(String name) {
        return StringUtils.isNotBlank(name);
    }

    @Override
    public Object get(String name, HttpServletRequest obj) {
        // name - attribute
        // $name - parameter
        // #name - method
        if (name.startsWith("$")) {
            // parameter
            return obj.getParameter(name.substring(1));
        } else if (name.startsWith("#")) {
            // method
            Method method = ReflectionUtils.findReadMethod(getType(), name.substring(1));
            if (method == null) {
                throw new IllegalArgumentException(name + " method not found");
            }
            return ReflectionUtils.invokeMethod(method, obj);
        }
        return obj.getAttribute(name.startsWith("\\") ? name.substring(1) : name);
    }

    @Override
    public void set(String name, HttpServletRequest obj, Object val) {
        obj.setAttribute(name, val);
    }

}
