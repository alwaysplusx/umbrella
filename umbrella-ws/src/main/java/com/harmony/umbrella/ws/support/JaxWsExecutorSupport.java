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

import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutor;

/**
 * 消息发送扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface JaxWsExecutorSupport extends JaxWsExecutor {

    /**
     * 发生消息
     * 
     * @param context
     *            消息上下文
     * @return 消息发生成功标志
     * @see com.harmony.umbrella.ws.jaxws.support.JaxWsContextSender#send(Context)
     */
    boolean send(Context context);

}
