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
package com.harmony.umbrella.core;

import static com.harmony.umbrella.utils.ClassUtils.*;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.harmony.umbrella.utils.Exceptions;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultInvoker implements Invoker, Serializable {

    private static final long serialVersionUID = 5408528044216944899L;
    private boolean validate = false;

    @Override
    public Object invoke(Object obj, Method method, Object[] args) throws InvokeException {
        if (validate) {
            Class<?>[] pattern = method.getParameterTypes();
            if (args.length != pattern.length) {
                throw new InvokeException("parameter length mismatch");
            }
            if (!typeEquals(pattern, args)) {
                throw new InvokeException("parameter type mismatch");
            }
        }
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            Throwable cause = Exceptions.getRootCause(e);
            throw new InvokeException(cause.getMessage(), cause);
        }
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
}
