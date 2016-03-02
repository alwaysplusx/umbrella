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
package com.harmony.umbrella.log.template;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.harmony.umbrella.log.Template;
import com.harmony.umbrella.log.TemplateFactory;

/**
 * @author wuxii@foxmail.com
 */
public class MessageTemplateFactory implements TemplateFactory {

    private ConcurrentMap<Method, Template> templateCache = new ConcurrentHashMap<Method, Template>();

    @Override
    public Template createTemplate(Method method) {
        Template template = templateCache.get(method);
        if (template == null) {
            template = new MessageTemplate(method);
            templateCache.putIfAbsent(method, template);
        }
        return templateCache.get(method);
    }

}
