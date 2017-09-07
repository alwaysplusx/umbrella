package com.harmony.umbrella.message.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.Message;

import com.harmony.umbrella.message.MessageResolver;

/**
 * @author wuxii@foxmail.com
 */
public class MessageResolverComposite implements MessageResolver {

    private List<MessageResolver> messageResolvers = new ArrayList<>();

    @Override
    public boolean support(Message message) {
        return getMessageResolver(message) != null;
    }

    @Override
    public void resolve(Message message) {
        MessageResolver messageResolver = getMessageResolver(message);
        if (messageResolver == null) {
            throw new IllegalStateException("unsupported message type");
        }
        messageResolver.resolve(message);
    }

    protected MessageResolver getMessageResolver(Message message) {
        for (MessageResolver messageResolver : messageResolvers) {
            if (messageResolver.support(message)) {
                return messageResolver;
            }
        }
        return null;
    }

    public void addMessageResolver(MessageResolver... messageResolvers) {
        this.messageResolvers.addAll(Arrays.asList(messageResolvers));
    }

    public List<MessageResolver> getMessageResolvers() {
        return messageResolvers;
    }

    public void setMessageResolvers(List<MessageResolver> messageResolvers) {
        this.messageResolvers = messageResolvers;
    }

}
