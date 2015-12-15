/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.context.spring;

import java.util.Map;
import java.util.Properties;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ContextProvider;

/**
 * @author wuxii@foxmail.com
 */
public class SpringContextProvider extends ContextProvider {

    @Override
    public ApplicationContext createApplicationContext() {
        return createApplicationContext(new Properties());
    }

    @Override
    public ApplicationContext createApplicationContext(Map<?, ?> properties) {
        return null;
    }

}
