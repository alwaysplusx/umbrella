package com.harmony.umbrella.log.resolver;

import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HttpResponseResolver extends CheckedResolver<HttpServletResponse> {

    public HttpResponseResolver(int priority) {
        super(HttpServletResponse.class, priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return StringUtils.isNotBlank(name) && obj instanceof HttpServletResponse;
    }

    @Override
    protected Object doResolve(String name, HttpServletResponse obj) {
        if (name.startsWith("#")) {
            try {
                return ReflectionUtils.invokeMethod(name.substring(1), obj);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("unresolver name " + name, e);
            }
        }
        return obj.getHeader(name);
    }

}
