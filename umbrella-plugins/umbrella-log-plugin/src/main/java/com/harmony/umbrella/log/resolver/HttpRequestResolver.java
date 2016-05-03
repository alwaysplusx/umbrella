package com.harmony.umbrella.log.resolver;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HttpRequestResolver extends CheckedResolver<HttpServletRequest> {

    public HttpRequestResolver(int priority) {
        super(HttpServletRequest.class, priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return StringUtils.isNotBlank(name) && obj instanceof HttpServletRequest;
    }

    @Override
    protected Object doResolve(String name, HttpServletRequest obj) {
        if (name.startsWith("#")) {
            try {
                return ReflectionUtils.invokeMethod(name.substring(1), obj);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("unresolver name " + name, e);
            }
        } else if (name.startsWith("$")) {
            return obj.getParameter(name.substring(1));
        }
        return obj.getAttribute(name);
    }

}
