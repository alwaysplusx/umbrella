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
package com.harmony.umbrella.ws.support;

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
        return "context message of " + context.toString();
    }

}
