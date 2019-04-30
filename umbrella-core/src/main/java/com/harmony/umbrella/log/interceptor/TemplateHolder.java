package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.annotation.Scope;
import com.harmony.umbrella.template.Template;

/**
 * @author wuxii
 */
public class TemplateHolder {

    private final Template<ScopeExpression> template;
    private Object value;

    public TemplateHolder(Template<ScopeExpression> template) {
        this.template = template;
    }

    public Template<ScopeExpression> getTemplate() {
        return template;
    }

    public boolean isSameScope(Scope scope) {
        return template.getExpression().getScope().equals(scope);
    }

    public void resolve(Object rootObject) {
        this.value = template.getValue(rootObject);
    }

    public Object getValue() {
        return value;
    }

}
