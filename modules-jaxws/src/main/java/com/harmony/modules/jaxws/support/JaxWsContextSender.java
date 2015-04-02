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
package com.harmony.modules.jaxws.support;

import com.harmony.modules.jaxws.JaxWsContext;

/**
 * 执行信号发送者
 * 
 * @author wuxii
 *
 */
public interface JaxWsContextSender {

    /**
     * 发送执行信号
     * 
     * @param context
     *            执行上下文
     * @return true发送成功, false发送失败
     */
    boolean send(JaxWsContext context);

    /**
     * 打开发送资源
     */
    void open();

    /**
     * 关闭资源
     * 
     * @throws Exception
     */
    void close() throws Exception;

    /**
     * 查看是否关闭
     * 
     * @return true已关闭,false未关闭
     */
    boolean isClosed();

}
