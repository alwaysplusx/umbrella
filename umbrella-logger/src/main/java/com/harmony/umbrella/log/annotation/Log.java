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
package com.harmony.umbrella.log.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.MessageTemplateFormat;

/**
 * @author wuxii@foxmail.com
 */
@Inherited
@Target({ METHOD })
@Retention(RUNTIME)
public @interface Log {

    /**
     * 模块
     * 
     * @return
     */
    String module() default "";

    /**
     * 操作名称
     * 
     * @return
     */
    String action() default "";

    /**
     * 日志级别
     * 
     * @return
     */
    Level level() default Level.INFO;

    /**
     * 日志消息
     * <p>
     * 可以通过模版的方式对消息日志进行装配
     * 
     * @return
     */
    String message() default "";

    /**
     * 需要带入消息模版的参数的位置
     * 
     * @return
     */
    int[] params() default {};

    /**
     * 消息格式化工具
     * 
     * @return
     */
    Class<? extends MessageTemplateFormat> formatClass();

}
