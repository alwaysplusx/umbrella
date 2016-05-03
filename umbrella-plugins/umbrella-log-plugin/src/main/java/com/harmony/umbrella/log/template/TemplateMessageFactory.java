package com.harmony.umbrella.log.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.el.Expression;
import com.harmony.umbrella.el.Template;
import com.harmony.umbrella.el.resolver.ArrayResolver;
import com.harmony.umbrella.el.resolver.ListResolver;
import com.harmony.umbrella.el.resolver.MapResolver;
import com.harmony.umbrella.el.resolver.NamedResolver;
import com.harmony.umbrella.el.resolver.PrimitiveArrayResolver;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.resolver.HttpRequestResolver;
import com.harmony.umbrella.log.resolver.HttpResponseResolver;
import com.harmony.umbrella.log.resolver.HttpServletContextResolver;
import com.harmony.umbrella.log.resolver.HttpSessionResolver;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class TemplateMessageFactory {

    private static final List<CheckedResolver<?>> defaultResolvers = new ArrayList<CheckedResolver<?>>();

    static {
        // http support
        defaultResolvers.add(new HttpRequestResolver(50));
        defaultResolvers.add(new HttpSessionResolver(50));
        defaultResolvers.add(new HttpResponseResolver(50));
        defaultResolvers.add(new HttpServletContextResolver(50));
        
        defaultResolvers.add(new PrimitiveArrayResolver(40));
        defaultResolvers.add(new ArrayResolver(30));
        defaultResolvers.add(new MapResolver(20));
        defaultResolvers.add(new ListResolver(10));
        defaultResolvers.add(new NamedResolver(0));
    }

    protected final Logging ann;
    private Template template;
    private Expression idExpression;

    private List<CheckedResolver<?>> checkedResolvers;

    public TemplateMessageFactory(Logging ann) {
        this(ann, defaultCheckedResolvers());
    }

    public TemplateMessageFactory(Logging ann, List<CheckedResolver<?>> checkedResolvers) {
        this.ann = ann;
        this.template = new Template(ann.message());
        this.idExpression = new Expression(ann.id());
        this.checkedResolvers = checkedResolvers;
    }

    public String newMessage(Holder holder) {
        if (template.isEmpty()) {
            return template.getTemplateText();
        }
        updateIterator();
        List<Object> args = new ArrayList<Object>(template.size());
        for (Expression expression : template) {
            args.add(getExpressionValue(expression, holder));
        }
        return template.format(args.toArray());
    }

    public Object getId(Holder holder) {
        if (StringUtils.isBlank(idExpression.getExpressionText())) {
            return null;
        }
        updateIterator();
        return getExpressionValue(idExpression, holder);
    }

    private Object getExpressionValue(Expression expression, Object val) {
        if (StringUtils.isBlank(expression.getExpressionText())) {
            return null;
        }
        Iterator<Expression> iterator = expression.iterator(".[]");
        while (iterator.hasNext()) {
            String expressionText = iterator.next().getExpressionText();
            Object tmp = getValue(expressionText, val);
            if (tmp == null && iterator.hasNext()) {
                throw new IllegalArgumentException("got null value from " + val + " " + expressionText);
            }
            val = tmp;
        }
        return val;
    }

    private Object getValue(String expression, Object val) {
        for (CheckedResolver<?> checkedResolver : checkedResolvers) {
            if (checkedResolver.support(expression, val)) {
                return checkedResolver.resolve(expression, val);
            }
        }
        throw new IllegalArgumentException(expression + " no suitable typed resolver");
    }

    private void updateIterator() {
        Collections.sort(checkedResolvers);
    }

    public Logging getLoggingAnnotation() {
        return ann;
    }

    public void addCheckedResolver(CheckedResolver<?> resolver) {
        this.checkedResolvers.add(resolver);
    }

    public List<CheckedResolver<?>> getCheckedResolvers() {
        return checkedResolvers;
    }

    public void setCheckedResolvers(List<CheckedResolver<?>> checkedResolvers) {
        this.checkedResolvers = checkedResolvers;
    }

    public static List<CheckedResolver<?>> defaultCheckedResolvers() {
        return new ArrayList<CheckedResolver<?>>(defaultResolvers);
    }

}
