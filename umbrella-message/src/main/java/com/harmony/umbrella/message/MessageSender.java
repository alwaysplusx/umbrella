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
     * 发送消息给消息中心, 或脱离{@linkplain MessageCenter}作为JMS使用
     * 
     * @param message
     * @return
     * @see javax.jms.MessageProducer#send(javax.jms.Message)
     */
    boolean send(Message message);

}