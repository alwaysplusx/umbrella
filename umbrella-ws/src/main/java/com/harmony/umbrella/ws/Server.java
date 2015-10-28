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
package com.harmony.umbrella.ws;

/**
 * 接口服务
 * 
 * @author wuxii@foxmail.com
 */
public interface Server {

    /**
     * 接口实例
     * 
     * @return 接口实际业务bean
     */
    Object getServiceBean();

    /**
     * 接口类
     * 
     * @return 接口class
     */
    Class<?> getServiceClass();

}
