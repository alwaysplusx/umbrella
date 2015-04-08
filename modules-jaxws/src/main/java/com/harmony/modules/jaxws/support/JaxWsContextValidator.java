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
 * @author wuxii@foxmail.com
 */
public abstract class JaxWsContextValidator {

    /**
     * 检查context是否符合交互最低要求
     * @param context
     * @throws IllegalArgumentException
     */
    public static void validation(JaxWsContext context) throws IllegalArgumentException {
        if (context.getServiceInterface() == null)
            throw new IllegalArgumentException("service interface not set");
        if (context.getAddress() == null)
            throw new IllegalArgumentException("service address not set");
        if (context.getMethodName() == null)
            throw new IllegalArgumentException("service method not set");
    }

}
