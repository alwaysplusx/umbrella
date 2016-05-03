package com.harmony.umbrella.monitor.attack;

import static com.harmony.umbrella.monitor.annotation.HttpProperty.Scope.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.monitor.HttpAttacker;
import com.harmony.umbrella.monitor.annotation.HttpProperty.Scope;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleHttpAttacker implements HttpAttacker {

    @Override
    public Map<String, Object> attack(HttpServletRequest request, String... names) {
        return attack(request, PARAMETER, names);
    }

    @Override
    public Map<String, Object> attack(HttpServletRequest request, Scope scope, String... names) {
        if (names == null || names.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<String, Object>();

        if (PARAMETER.equals(scope)) {
            for (String name : names) {
                result.put(name, request.getParameter(name));
            }
        } else if (REQUEST.equals(scope)) {
            for (String name : names) {
                result.put(name, request.getAttribute(name));
            }
        } else if (SESSION.equals(scope)) {
            for (String name : names) {
                result.put(name, request.getSession().getAttribute(name));
            }
        }
        return result;
    }
}
