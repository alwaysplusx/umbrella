package com.harmony.umbrella.monitor.attack;

import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.monitor.Attacker;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * @author wuxii@foxmail.com
 */
public class OgnlAttacker implements Attacker<ActionSupport> {

    @Override
    public Map<String, Object> attack(ActionSupport target, String... names) {
        Map<String, Object> result = new HashMap<String, Object>();
        ActionContext context = ActionContext.getContext();
        ValueStack stack = context.getValueStack();
        for (String name : names) {
            result.put(name, stack.findValue(name));
        }
        return result;
    }
}
