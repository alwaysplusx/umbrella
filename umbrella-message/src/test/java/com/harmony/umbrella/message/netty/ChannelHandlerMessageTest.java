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
package com.harmony.umbrella.message.netty;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class ChannelHandlerMessageTest {

    @Before
    public void setUp() {
        // new ChannelHandlerMessageListener().init();
    }

    @Test
    public void test() {
        ChannelHandlerMessageSender sender = new ChannelHandlerMessageSender("localhost", 8080);
        sender.send(new ChannelMessage("wuxii"));
    }

    @After
    public void treaDown() throws Exception {
        // Thread.sleep(Long.MAX_VALUE);
    }

}
