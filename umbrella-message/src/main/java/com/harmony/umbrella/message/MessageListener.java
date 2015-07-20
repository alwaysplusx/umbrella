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
 * 监听消息
 *
 * @author wuxii
 * @see javax.jms.MessageListener
 */
public interface MessageListener {

    /**
     * 资源初始化
     */
    void init();

    /**
     * 当消息到达时候触发
     *
     * @param message
     *         接受的消息
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    void onMessage(Message message);

    /**
     * 清空占用的资源
     */
    void destroy();

}
