package com.harmony.umbrella.template.spel;

import com.harmony.umbrella.template.Expressions;
import com.harmony.umbrella.template.Template;
import com.harmony.umbrella.template.TemplateItem;

import java.util.List;

/**
 * @author wuxii
 */
class SpelTemplate implements Template {

    private Expressions expressions;
    private List<TemplateItem> items;

    public SpelTemplate(Expressions expressions, List<TemplateItem> items) {
        this.expressions = expressions;
        this.items = items;
    }

    @Override
    public Expressions getExpressions() {
        return expressions;
    }

    @Override
    public String getValue(Object rootObject) {
        StringBuilder out = new StringBuilder();
        for (TemplateItem item : items) {
            out.append(item.getValue(rootObject));
        }
        return out.toString();
    }

    @Override
    public List<TemplateItem> getTemplateItems() {
        return items;
    }

}
