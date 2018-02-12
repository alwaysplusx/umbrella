package com.harmony.umbrella.message.support;

import com.harmony.umbrella.message.MessageTemplate;
import com.harmony.umbrella.message.MessageTemplateBuilder;
import com.harmony.umbrella.message.MessageTemplateFactory;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleMessageTemplateFactory implements MessageTemplateFactory {

    @Override
    public MessageTemplate createMessageTemplate(TemplateConfig cfg) {
        checkTemplate(cfg);
        MessageTemplateBuilder builder = newMessageTemplateBuilder();
        return applyTemplateConfig(builder, cfg).build();
    }

    protected <T extends MessageTemplateBuilder<T>> T applyTemplateConfig(T builder, TemplateConfig cfg) {
        return builder.apply(cfg);
    }

    protected <T extends MessageTemplateBuilder<T>> T newMessageTemplateBuilder() {
        return (T) MessageTemplateBuilder.newBuilder();
    }

    protected void checkTemplate(TemplateConfig cfg) {
        if (cfg == null) {
            throw new IllegalArgumentException("template must been not null");
        }
        if (cfg.getDestination() == null) {
            throw new IllegalArgumentException("no destination for " + cfg);
        }
        if (cfg.getConnectionFactory() == null) {
            throw new IllegalArgumentException("no connection factory for " + cfg);
        }
    }

}
