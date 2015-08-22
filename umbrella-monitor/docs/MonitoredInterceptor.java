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
package com.harmony.umbrella.monitor.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import com.harmony.umbrella.monitor.annotation.Monitored.Level;

/**
 * 此注解配合CDI使用. /META-INF/beans.xml
 */
@Inherited
@Monitored
@InterceptorBinding
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface MonitoredInterceptor {

    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     */
    String operator() default "";

    /**
     * 对应日志的级别
     */
    Level level() default Level.INFO;

    /**
     * 代理对象的内部参数获取工具
     */
    InternalProperty[] internalProperties() default {};

    /**
     * http监控获取request中的信息
     */
    HttpProperty[] httpProperties() default {};
}
