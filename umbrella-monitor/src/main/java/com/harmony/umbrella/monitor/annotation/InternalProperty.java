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

import com.harmony.umbrella.monitor.Attacker;

/**
 * 获取代理对象的内部数据
 * 
 * @author wuxii@foxmail.com
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface InternalProperty {

    /**
     * 监控对象内容处理工具类
     */
    Class<? extends Attacker> attacker();

    /**
     * 监控的内部属性或无参get方法的名称
     */
    String[] properties() default {};

    /**
     * 何时拦截
     */
    Mode mode() default Mode.INOUT;
}