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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 需要监控的Http内部信息
 *
 * @author wuxii@foxmail.com
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface HttpProperty {

    /**
     * http的参数作用域, 一个方法上至多配置一个scope，如果有重复的则最先配置的生效
     */
    Scope scope() default Scope.PARAMETER;

    /**
     * 需要获取的值
     */
    String[] properties();

    /**
     * 何时拦截
     */
    Mode mode() default Mode.IN;

    /**
     * 对应Http的作用域
     *
     * @author wuxii@foxmail.com
     */
    public enum Scope {
        /**
         * 对应于
         * {@linkplain javax.servlet.http.HttpServletRequest#getParameter(String)}
         */
        PARAMETER,
        /**
         * 对应于
         * {@linkplain javax.servlet.http.HttpServletRequest#getAttribute(String)}
         */
        REQUEST,
        /**
         * 对应于{@linkplain javax.servlet.http.HttpServletRequest#getSession()}
         */
        SESSION
    }

}
