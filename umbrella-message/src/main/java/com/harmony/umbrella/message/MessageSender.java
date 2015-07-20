/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
 * @author wuxii@foxmail.com
 */
public interface MessageSender {

    /**
     * 发送消息给消息中心, 作为JMS使用
     *
     * @param message
     *         需要发送的消息
     * @return if return {@code true} 发送成功
     * @see javax.jms.MessageProducer#send(javax.jms.Message)
     */
    boolean send(Message message);

}
