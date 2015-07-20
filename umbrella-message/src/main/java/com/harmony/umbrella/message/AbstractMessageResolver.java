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

/**
 * 消息解析抽象类
 *
 * @author wuxii
 */
public abstract class AbstractMessageResolver<T> implements MessageResolver {

    @Override
    public void handle(Message message) {
        process(resolver(message));
    }

    /**
     * 处理解析后的消息
     *
     * @param message
     *         待处理的消息
     */
    public abstract void process(T message);

    /**
     * 将消息解析为对应的实际内容
     *
     * @param message
     *         待解析的消息
     * @return 解析后的消息
     */
    protected abstract T resolver(Message message);

}
