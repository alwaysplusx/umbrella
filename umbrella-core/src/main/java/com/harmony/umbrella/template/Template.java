package com.harmony.umbrella.template;

import java.util.List;

/**
 * @author wuxii
 */
public interface Template {

    Expressions getExpressions();

    String getValue(Object rootObject);

    List<TemplateItem> getTemplateItems();

}
