/*
 * Copyright 2002-2015 the original author or authors.
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

import java.util.List;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMessageListener implements MessageListener {

    protected static final Log LOG = Logs.getLog(AbstractMessageListener.class);

    public AbstractMessageListener() {
    }

    protected abstract List<MessageResolver> getMessageResolvers();

    /**
     * 接受到的消息, 使用{@linkplain MessageResolver#support(Message)}判定当前有哪些是符合条件的
     * {@linkplain MessageResolver}. 再经由
     * {@linkplain MessageResolver#handle(Message)}处理该消息.
     * <p/>
     * <p>
     * 消息是可以被多个{@linkplain MessageResolver}按顺序处理的
     *
     * @see MessageResolver
     */
    @Override
    public void onMessage(Message message) {
        for (MessageResolver mr : getMessageResolvers()) {
            if (mr.support(message)) {
                LOG.debug("{}处理消息{}", mr, message);
                mr.handle(message);
            }
        }
    }

}
