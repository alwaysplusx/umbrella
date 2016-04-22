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
package com.harmony.umbrella.ws.support;

import com.harmony.umbrella.ws.Context;

/**
 * 执行信号发送者
 *
 * @author wuxii@foxmail.com
 */
public interface ContextSender {

    /**
     * 发送执行信号
     *
     * @param context
     *            执行上下文
     * @return true发送成功, false发送失败
     */
    boolean send(Context context);

}
