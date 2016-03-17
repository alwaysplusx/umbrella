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
package com.harmony.umbrella.log.interceptor;

import javax.annotation.PostConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.TemplateFactory;
import com.harmony.umbrella.log.template.MessageTemplateFactory;

/**
 * @author wuxii@foxmail.com
 */
@Interceptor
public class EJBLoggingInterceptor extends AbstractLoggingInterceptor<InvocationContext> {

    protected TemplateFactory templateFactory;

    @PostConstruct
    private void postConstruct() {
        this.init();
        this.templateFactory = new MessageTemplateFactory();
    }

    @AroundInvoke
    public Object interceptor(InvocationContext ctx) throws Exception {
        return logging(ctx);
    }

    @Override
    protected com.harmony.umbrella.log.interceptor.InvocationContext convert(InvocationContext invocationContext) {
        return new EJBInvocationContext(invocationContext);
    }

    @Override
    protected Message newMessage(com.harmony.umbrella.log.interceptor.InvocationContext ctx) {
        return templateFactory.createTemplate(ctx.method).newMessage(ctx.target, ctx.result, ctx.parameters);
    }

    private static final class EJBInvocationContext extends com.harmony.umbrella.log.interceptor.InvocationContext {

        private final InvocationContext invocationContext;

        public EJBInvocationContext(InvocationContext context) {
            super(context.getTarget(), context.getMethod(), context.getParameters());
            this.invocationContext = context;
        }

        @Override
        protected Object doProcess() throws Exception {
            return invocationContext.proceed();
        }
    }

}
