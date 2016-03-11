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

import java.net.URL;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ContextProvider;

/**
 * @author wuxii@foxmail.com
 */
public class SpringContextProvider extends ContextProvider {

    @Override
    public ApplicationContext createApplicationContext() {
        return createApplicationContext(null    );
    }

    @Override
    public ApplicationContext createApplicationContext(URL url) {
        Assert.notNull(SpringContextHolder.springApplication, "spring application context not set, configuration spring bean class" + SpringContextHolder.class);
        return new SpringApplicationContext(SpringContextHolder.springApplication, url);
    }

    public static class SpringContextHolder implements ApplicationContextAware {

        private static org.springframework.context.ApplicationContext springApplication;

        @Override
        public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
            if (springApplication == null) {
                SpringContextHolder.springApplication = applicationContext;
            }
        }

    }

}
