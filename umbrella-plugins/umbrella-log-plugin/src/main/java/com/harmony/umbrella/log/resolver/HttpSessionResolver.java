package com.harmony.umbrella.log.resolver;

import javax.servlet.http.HttpSession;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HttpSessionResolver extends CheckedResolver<HttpSession> {

    public HttpSessionResolver(int priority) {
        super(HttpSession.class, priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return StringUtils.isNotBlank(name) && obj instanceof HttpSession;
    }

    @Override
    protected Object doResolve(String name, HttpSession obj) {
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
