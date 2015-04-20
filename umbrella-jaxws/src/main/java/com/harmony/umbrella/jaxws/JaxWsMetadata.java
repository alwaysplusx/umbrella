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
package com.harmony.umbrella.jaxws;

/**
 * 可将jaxws属性保存到数据库或者其他地方做的扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface JaxWsMetadata {

    /**
     * 服务名
     * 
     * @return
     */
    String getServiceName();

    /**
     * 服务接口类
     * 
     * @return
     */
    Class<?> getServiceClass();

    /**
     * 服务所在的地址
     * 
     * @return
     */
    String getAddress();

    /**
     * 访问服务所需要使用的用户名. 非必须
     * 
     * @return
     */
    String getUsername();

    /**
     * 范文服务所需要的密码. 非必须
     * 
     * @return
     */
    String getPassword();

}
