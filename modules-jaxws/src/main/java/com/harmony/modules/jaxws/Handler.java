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
package com.harmony.modules.jaxws;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为class标记{@linkplain Handler},表示被标记的class为{@linkplain JaxWsContext}的处理类
 * 
 * @author wuxii@foxmail.com
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

	/**
	 * 关联处理的类{@linkplain JaxWsContext#getServiceInterface()}
	 */
	Class<?>[] value() default {};

	/**
	 * 关联处理的类{@linkplain JaxWsContext#getServiceInterface()}
	 */
	String[] handles() default {};

	/**
	 * 为方法标记{@linkplain HandleMethod}，表示为一个{@linkplain Handler}处理方法.
	 * 
	 * <pre>
	 * 各个Phase对应的方法说明
	 * <table border="2" rules="all" cellpadding="4">
	 * 	<thead>
	 * 		<tr>
	 * 			<th align="center" colspan="5">Phase对应方法参数说明</th>
	 * 		</tr>
	 * 	</thead>
	 * 	<tbody>
	 * 		<tr>
	 * 			<th>Phase</th>
	 * 			<th>Parameter Types</th>
	 * 			<th>Context Map Parameter Types</th>
	 * 			<th>Return type</th>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>PRE_INVOKER</td>
	 * 			<td>{@linkplain JaxWsContext#getParameters()}</td>
	 * 			<td>{@linkplain JaxWsContext#getParameters()} + {@linkplain Map} </td>
	 * 			<td>Boolean or void</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>ABORT</td>
	 * 			<td>@{@linkplain JaxWsAbortException} + {@linkplain JaxWsContext#getParameters()}</td>
	 * 			<td>{@linkplain JaxWsAbortException} + {@linkplain JaxWsContext#getParameters()} + {@linkplain Map}</td>
	 * 			<td>void</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>POST_INVOKE</td>
	 * 			<td>{@linkplain JaxWsContext#getMethod()}的返回类型 + {@linkplain JaxWsContext#getParameters()}</td>
	 * 			<td>{@linkplain JaxWsContext#getMethod()}的返回类型 + {@linkplain JaxWsContext#getParameters()} + {@linkplain Map}</td>
	 * 			<td>void</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>THROWING</td>
	 * 			<td>{@linkplain Throwable} + {@linkplain JaxWsContext#getParameters()}</td>
	 * 			<td>{@linkplain Throwable} + {@linkplain JaxWsContext#getParameters()} + {@linkplain Map}</td>
	 * 			<td>void</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>FINALLY</td>
	 * 			<td>{@linkplain Throwable} + {@linkplain JaxWsContext#getMethod()}的返回类型 + {@linkplain JaxWsContext#getParameters()}</td>
	 * 			<td>{@linkplain Throwable} + {@linkplain JaxWsContext#getMethod()}的返回类型 + {@linkplain JaxWsContext#getParameters()} + {@linkplain Map}</td>
	 * 			<td>void</td>
	 * 		</tr>
	 * 	</tbody>
	 * 	</table>
	 * </pre>
	 * 
	 */
	@Documented
	@Target({ ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface HandleMethod {

		String methodName() default "";

		Phase phase();

	}

}
