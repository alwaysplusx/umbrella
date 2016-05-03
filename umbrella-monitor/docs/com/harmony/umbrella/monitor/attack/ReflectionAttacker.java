package com.harmony.umbrella.monitor.attack;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.monitor.Attacker;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ReflectionAttacker implements Attacker<Object> {

    @Override
    public Map<String, Object> attack(Object target, String... names) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String name : names) {
            try {
                Method method = ReflectionUtils.findReadMethod(target.getClass(), name);
                result.put(name, ReflectionUtils.invokeMethod(method, target));
            } catch (Exception e) {
            }
        }
        return result;
    }
}
