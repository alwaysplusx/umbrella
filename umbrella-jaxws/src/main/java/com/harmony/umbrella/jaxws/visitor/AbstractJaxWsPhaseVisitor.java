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
package com.harmony.umbrella.jaxws.visitor;

import com.harmony.umbrella.jaxws.JaxWsAbortException;
import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.JaxWsGraph;
import com.harmony.umbrella.jaxws.JaxWsPhaseVisitor;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractJaxWsPhaseVisitor implements JaxWsPhaseVisitor {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean visitBefore(JaxWsContext context) throws JaxWsAbortException {
        return true;
    }

    @Override
    public void visitAbort(JaxWsAbortException ex, JaxWsContext context) {
    }

    @Override
    public void visitCompletion(Object result, JaxWsContext context) {
    }

    @Override
    public void visitThrowing(Throwable throwable, JaxWsContext context) {
    }

    @Override
    public void visitFinally(Object result, Throwable throwable, JaxWsGraph graph, JaxWsContext context) {
    }

}
