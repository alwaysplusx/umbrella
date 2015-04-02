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
package com.harmony.modules.jaxws.invoker;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultInvoker implements Invoker, Serializable {

    private static final long serialVersionUID = 5408528044216944899L;

    @Override
    public Object invok(Object obj, Method method, Object[] args) throws InvokException {
        try {
            return method.invoke(obj, args);
        } catch (IllegalArgumentException e) {
            throw new InvokException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new InvokException(e.getCause());
        } catch (InvocationTargetException e) {
            throw new InvokException(e.getCause());
        }
    }

}
