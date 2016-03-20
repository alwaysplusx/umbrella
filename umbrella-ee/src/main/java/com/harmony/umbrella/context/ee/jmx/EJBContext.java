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
package com.harmony.umbrella.context.ee.jmx;

/**
 * @author wuxii@foxmail.com
 */
public class EJBContext implements EJBContextMBean {

    private final EJBContextMBean context;

    public EJBContext(EJBContextMBean context) {
        this.context = context;
    }

    @Override
    public void loadProperties() {
        context.loadProperties();
    }

    @Override
    public void clearProperties() {
        context.clearProperties();
    }

    @Override
    public boolean exists(String className) {
        return context.exists(className);
    }

    @Override
    public String showProperties() {
        return context.showProperties();
    }

    @Override
    public String jndiPropertiesFileLocation() {
        return context.jndiPropertiesFileLocation();
    }

}
