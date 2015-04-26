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
package com.harmony.umbrella.context.ee.jmx;

/**
 * @author wuxii@foxmail.com
 */
public interface EJBContextMBean {

    /**
     * 加载属性文件地址为jndi.properties
     */
    void loadProperties();

    /**
     * 清除已经加载的属性
     */
    void cleanProperties();

    /**
     * 查看是否存在类型为clazz的会话bean
     * 
     * @return
     */
    boolean exixts(String className);

    /**
     * 当前上下文属性文件所在位置
     * 
     * @return
     */
    String jndiPropertiesFilePath();

}
