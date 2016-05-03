package com.harmony.umbrella.message.netty;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
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
