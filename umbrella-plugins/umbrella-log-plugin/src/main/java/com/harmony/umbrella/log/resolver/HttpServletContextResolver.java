package com.harmony.umbrella.log.resolver;

import javax.servlet.ServletContext;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HttpServletContextResolver extends CheckedResolver<ServletContext> {

    public HttpServletContextResolver(int priority) {
        super(ServletContext.class, priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return StringUtils.isNotBlank(name) && obj instanceof ServletContext;
    }

    @Override
    protected Object doResolve(String name, ServletContext obj) {
        if (name.startsWith("#")) {
            try {
                return ReflectionUtils.invokeMethod(name.substring(1), obj);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("unresolver name " + name, e);
            }
        }
        return obj.getAttribute(name);
    }

}
