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
 * EJB Application Context JMX管理扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface EJBContextMBean {

    /**
     * 清除已经加载的属性
     */
    void resetProperties();

    /**
     * 查看是否存在类型为clazz的会话bean
     */
    boolean exists(String className);

    /**
     * 当前上下文属性文件所在位置
     */
    String propertiesFileLocation();

    /**
     * 展示现在所有的资源属性
     */
    String showProperties();

}
