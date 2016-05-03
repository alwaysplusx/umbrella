package com.harmony.umbrella.el;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class ExpressionResolverChain {

    protected List<CheckedResolver<?>> checkedResolvers;

    public Object doChain(Expression expression, Object val) {
        Iterator<String> iterator = expression.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            Object tmp = getTokenValue(token, val);
            if (val == null && iterator.hasNext()) {
                throw new IllegalArgumentException("got null value from " + val + " " + token);
            }
            val = tmp;
        }
        return val;
    }

    private Object getTokenValue(String token, Object val) {
        for (CheckedResolver<?> checkedResolver : checkedResolvers) {
            if (checkedResolver.support(token, val)) {
                return checkedResolver.resolve(token, val);
            }
        }
        throw new IllegalArgumentException(" cannot resolver " + val + " " + token);
    }

    private void updateIterator() {
        Collections.sort(checkedResolvers);
    }

    public void addCheckedResolver(CheckedResolver<?> resolver) {
        this.checkedResolvers.add(resolver);
    }

    public void setCheckedResolvers(List<CheckedResolver<?>> checkedResolvers) {
        this.checkedResolvers = checkedResolvers;
    }

    public List<CheckedResolver<?>> getCheckedResolvers() {
        updateIterator();
        return checkedResolvers;
    }

}
