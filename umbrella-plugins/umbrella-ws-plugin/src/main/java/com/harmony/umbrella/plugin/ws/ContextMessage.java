package com.harmony.umbrella.plugin.ws;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.ws.Context;

/**
 * marker message to ContextMessage
 * 
 * @author wuxii@foxmail.com
 */
public class ContextMessage implements Message {

    private static final long serialVersionUID = 3096037044199974769L;

    private Context context;

    public ContextMessage() {
    }

    public ContextMessage(Context context) {
        Assert.notNull(context, "context message is null");
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "context message of " + context;
    }

}
