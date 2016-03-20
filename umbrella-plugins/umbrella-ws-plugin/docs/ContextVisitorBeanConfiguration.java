/*
 * Copyright 2012-2016 the original author or authors.
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
package com.harmony.umbrella.ws;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.context.ee.EEConfiguration;
import com.harmony.umbrella.ws.visitor.SyncableContextVisitor;

/**
 * @author wuxii@foxmail.com
 */
@Remote(EEConfiguration.class)
@Stateless(mappedName = "ContextVisitorBeanConfiguration")
public class ContextVisitorBeanConfiguration implements EEConfiguration<ContextVisitor> {

    @Override
    public List<ContextVisitor> getConfigBeans() {
        return Arrays.<ContextVisitor> asList(new SyncableContextVisitor());
    }

}
