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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebParam.Mode;

public class JaxWsInvoker extends DefaultInvoker implements Invoker {

    private static final long serialVersionUID = 8613526886152167871L;

    @Override
    public Object invok(Object target, Method method, Object[] parameters) throws InvokException {
        Object result = super.invok(target, method, parameters);
        if (result == null && method.getReturnType().equals(void.class)) {
            result = getParamOut(method, parameters);
        }
        return result;
    }

    private Object getParamOut(Method method, Object[] parameters) {
        List<Object> list = new ArrayList<Object>();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] anns = annotations[i];
            for (Annotation ann : anns) {
                if (ann instanceof WebParam) {
                    Mode mode = ((WebParam) ann).mode();
                    if (mode.equals(Mode.INOUT) || mode.equals(Mode.OUT)) {
                        list.add(parameters[i]);
                        // break;
                    }
                }
            }
        }
        return list.isEmpty() ? null : list.size() == 1 ? list.get(0) : list;
    }
}
