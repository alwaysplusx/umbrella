/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.message;

import com.harmony.umbrella.util.GenericUtils;

/**
 * 消息解析抽象类
 *
 * @param <T>
 *            实际的message类型
 * @author wuxii@foxmail.com
 */
public abstract class TypedMessageResolver<T extends Message> implements MessageResolver {

    private Class<T> messageType;

    public TypedMessageResolver() {
    }

    public TypedMessageResolver(Class<T> messageType) {
        this.messageType = messageType;
    }

    @Override
    public boolean support(Message message) {
        return getMessageType() == message.getClass();
    }

    @Override
    public void resolve(Message message) {
        process(convert(message));
    }

    /**
     * 处理解析后的消息
     *
     * @param message
     *            待处理的消息
     */
    public abstract void process(T message);

    @SuppressWarnings("unchecked")
    protected Class<T> getMessageType() {
        if (messageType == null) {
            messageType = (Class<T>) GenericUtils.getSuperGeneric(getClass(), 0);
        }
        return messageType;
    }

    /**
     * 将消息解析为对应的实际内容
     *
     * @param message
     *            待解析的消息
     * @return 解析后的消息
     */
    protected T convert(Message message) {
        Class<T> messageType = getMessageType();
        if (messageType.isInstance(message)) {
            return messageType.cast(message);
        }
        throw new IllegalStateException("illegal message type " + message);
    }
}
