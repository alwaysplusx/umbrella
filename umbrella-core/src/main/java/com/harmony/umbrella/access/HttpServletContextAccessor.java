package com.harmony.umbrella.access;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import com.harmony.umbrella.access.CheckedAccessor;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HttpServletContextAccessor extends CheckedAccessor<ServletContext> {

    public HttpServletContextAccessor() {
        super(ServletContext.class);
    }

    @Override
    public boolean support(String name) {
        return StringUtils.isNotBlank(name);
    }

    @Override
    public Object get(String name, ServletContext obj) {
        if (name.startsWith("#")) {
            Method method = ReflectionUtils.findReadMethod(getType(), name.substring(1));
            if (method == null) {
                throw new IllegalArgumentException(name + " method not found");
            }
            return ReflectionUtils.invokeMethod(method, obj);
        }
        return obj.getAttribute(name.startsWith("\\") ? name.substring(1) : name);
    }

    @Override
    public void set(String name, ServletContext obj, Object val) {
        obj.setAttribute(name, val);
    }

}
