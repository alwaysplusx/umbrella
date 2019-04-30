package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.annotation.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wuxii
 */
public class LogMessageContext {

    private List<TemplateHolder> messageTemplates;
    private TemplateHolder keyTemplate;

    public LogMessageContext(List<TemplateHolder> messageTemplates, TemplateHolder keyTemplate) {
        this.messageTemplates = Collections.unmodifiableList(messageTemplates);
        this.keyTemplate = keyTemplate;
    }

    public void handle(Scope scope, Object rootObject) {
        ArrayList<TemplateHolder> templates = new ArrayList<>(messageTemplates);
        if (keyTemplate != null) {
            templates.add(keyTemplate);
        }

        for (TemplateHolder template : templates) {
            if (template.isSameScope(scope)) {
                template.resolve(rootObject);
            }
        }
    }

    public String getFormattedMessage() {
        StringBuilder message = new StringBuilder();
        for (TemplateHolder template : messageTemplates) {
            message.append(template.getValue());
        }
        return message.toString();
    }

    public Object getFormattedKey() {
        return keyTemplate != null ? keyTemplate.getValue() : null;
    }

    public List<TemplateHolder> getMessageTemplates() {
        return messageTemplates;
    }

    public TemplateHolder getKeyTemplate() {
        return keyTemplate;
    }

}
