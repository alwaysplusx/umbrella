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
package com.harmony.umbrella.message.jms;

import javax.jms.Message;

import com.harmony.umbrella.message.MessageException;

/**
 * @author wuxii@foxmail.com
 */
public interface JmsConfig {

    String DEFAULT_CONNECTION_FACTORY = "jms/connectionFactory";

    String DEFAULT_QUEUE = "jms/queue";

    String DEFAULT_TOPIC = "jms/topic";

    String DEFAULT_DESTINATION = DEFAULT_QUEUE;

    void configMessage(Message message) throws MessageException;

    int sessionMode();

    boolean transacted();

    String username();

    String password();

}
