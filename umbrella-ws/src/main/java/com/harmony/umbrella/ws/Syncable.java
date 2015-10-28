/*
 * Copyright 2002-2015 the original author or authors.
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注业务为一个同步业务bean
 * <p>
 * 可以配置以下信息
 * <ul>
 * <li>endpoint 对应的接口
 * <li>methodName 接口业务方法
 * <li>address 接口的地址(可选)
 * <li>username 用户名(可选)
 * <li>password 密码(可选)
 * <li>connectionTimeout 连接等待时间(可选)
 * <li>receiveTimeout 接收等待时间(可选)
 * <li>synchronousTimeout 同步等待时间(可选)
 * </ul>
 * 
 * @author wuxii@foxmail.com
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Syncable {

    /**
     * 同步的服务类名
     */
    Class<?> endpoint();

    /**
     * 同步的方法名称
     */
    String methodName();

    /**
     * 服务的地址
     */
    String address() default "";

    /**
     * 服务的用户名
     */
    String username() default "";

    /**
     * 服务密码
     */
    String password() default "";

    /**
     * http 连接超时等待时间
     */
    long connectionTimeout() default -1;

    /**
     * 接收等待时间
     */
    long receiveTimeout() default -1;

    /**
     * 客户端同步等待时间
     */
    int synchronousTimeout() default -1;

    /**
     * 是否开启业务上的回调
     */
    boolean callback() default true;

}
