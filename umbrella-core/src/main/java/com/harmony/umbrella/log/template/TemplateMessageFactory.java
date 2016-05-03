package com.harmony.umbrella.log.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.message.SimpleMessage;

/**
 * @author wuxii@foxmail.com
 */
public class TemplateMessageFactory {

    protected List<CheckedResolver<?>> checkedResolvers;

    public TemplateMessageFactory(List<CheckedResolver<?>> resolvers) {
        this.checkedResolvers = resolvers;
    }

    public Template newTemplate(String templateText) {
        return new Template(templateText);
    }

    public Template newTemplate(String templateText, char delimiterStart, char delimiterEnd) {
        return new Template(templateText, delimiterStart, delimiterEnd);
    }

    public Message newMessage(String templateText, Holder holder) {
        Template template = new Template(templateText);
        List<Object> args = new ArrayList<Object>();
        for (TemplateExpression te : template) {
            if (!te.isText()) {
                te.getExpressionText();
            }
        }
        return new SimpleMessage(template.format(args.toArray()));
    }

    public Object getTemplateExpressionValue(TemplateExpression templateExpression, Holder holder) {
        Iterator<String> iterator = templateExpression.iterator();
        Object val = holder;
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

    public void setCheckedResolvers(List<CheckedResolver<?>> checkedResolvers) {
        this.checkedResolvers = checkedResolvers;
    }

    public List<CheckedResolver<?>> getCheckedResolvers() {
        return checkedResolvers;
    }

}
