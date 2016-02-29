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
package com.harmony.umbrella.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessageFactory;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.Message;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class Log4J2Test {

    public static void main(String[] args) {
        Logger log = LogManager.getLogger();
        log.info("i am message of {}", "Hello World");
    }

    @Test
    public void formatFactoryTest() {
        MessageFactory factory = new FormattedMessageFactory();
        Message message = factory.newMessage("i am message of {}", "Hello World");

        System.out.println(message);

        System.out.println(message.getFormat());
        System.out.println(message.getFormattedMessage());
    }

}
