package com.harmony.umbrella.message.netty;

import com.harmony.umbrella.message.Message;

/**
 * @author wuxii@foxmail.com
 */
public class ChannelMessage implements Message {

    private static final long serialVersionUID = 8306554032512496777L;

    public final String name;

    public ChannelMessage(String name) {
        this.name = name;
    }

}
