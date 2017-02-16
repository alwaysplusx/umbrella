package com.harmony.umbrella.util;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class SafetyClassFilter implements ClassFilter {

    private static final Log log = Logs.getLog(SafetyClassFilter.class);

    private final ClassFilter filter;

    public SafetyClassFilter(ClassFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(Class<?> clazz) {
        try {
            return filter.accept(clazz);
        } catch (Error e) {
            log.error("unable accept class", e);
        } catch (Throwable e) {
            log.error("unable accept class", e);
        }
        return false;
    }

}
