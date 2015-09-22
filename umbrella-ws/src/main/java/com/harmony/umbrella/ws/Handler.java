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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为class标记{@linkplain Handler},表示被标记的class为{@linkplain Context}的处理类
 * 
 * @author wuxii@foxmail.com
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

    /**
     * 关联处理的类{@linkplain Context#getServiceInterface()}
     */
    Class<?>[] value() default {};

    /**
     * 关联处理的类{@linkplain Context#getServiceInterface()}
     */
    String[] handles() default {};

    /**
     * class的排序信息，放在第几个回调位置
     */
    int ordinal() default 0;

    /**
     * 为方法标记{@linkplain HandleMethod}，表示为一个{@linkplain Handler}处理方法.
     * <p>
     * 并支持各个周期中获取执行上下文的context各个Phase对应的方法说明(该参数是可选项)
     * 
     * <pre>
     *  <table border="2" rules="all" cellpadding="4">
     *    <thead> 
     *      <tr><th align="center" colspan="5">Phase对应方法参数说明</th></tr>
     *    </thead> 
     *    <tbody>
     *      <tr>
     *          <th>Phase</th>
     *          <th>Parameter Types</th> 
     *          <th>Support Context Map</th>
     *          <th>Return type</th>
     *      </tr>
     *      <tr>
     *          <td>PRE_INVOKER</td>
     *          <td>{@linkplain Context#getParameters()}</td>
     *          <td>true</td>
     *          <td>Boolean or void</td> 
     *      </tr>
     *      <tr>
     *          <td>ABORT</td>
     *          <td>{@linkplain WebServiceAbortException}, {@linkplain Context#getParameters()}</td>
     *          <td>true</td>
     *          <td>void</td>
     *      </tr>
     *      <tr>
     *          <td>POST_INVOKE</td>
     *          <td>{@linkplain Context#getMethod()}的返回类型, {@linkplain Context#getParameters()}</td>
     *          <td>true</td>
     *          <td>void</td>
     *      </tr>
     *      <tr>
     *          <td>THROWING</td>
     *          <td>{@linkplain Throwable}, {@linkplain Context#getParameters()}</td>
     *          <td>true</td>
     *          <td>void</td>
     *      </tr>
     *      <tr>
     *          <td>FINALLY</td>
     *          <td>{@linkplain Throwable}, {@linkplain Context#getMethod()}的返回类型, {@linkplain Context#getParameters()}</td>
     *          <td>true</td>
     *          <td>void</td>
     *      </tr>
     *    </tbody>
     *  </table>
     * </pre>
     * 
     */
    @Documented
    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface HandleMethod {

        /**
         * 接口的方法名，在
         * {@linkplain com.harmony.umbrella.ws.visitor.PhaseValidationVisitor}
         * 默认为注解在方法体上的方法名
         */
        String methodName() default "";

        /**
         * 执行的周期{@linkplain Phase}
         */
        Phase phase();

    }

}
