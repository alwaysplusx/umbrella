/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.modules.monitor;

import java.util.Calendar;
import java.util.Map;

/**
 * 监视记录
 * @author wuxii@foxmail.com
 */
public interface Graph {

	/**
	 * 监视的资源标识
	 * @return
	 */
	String getIdentifie();

	/**
	 * 请求时间
	 * @return
	 */
	Calendar getRequestTime();

	/**
	 * 系统应答时间
	 * @return
	 */
	Calendar getResponseTime();

	/**
	 * 处理结果
	 * @return
	 */
	Object getResult();

	/**
	 * 请求参数
	 * @return
	 */
	Map<String, Object> getArguments();

	/**
	 * 请求中是否有异常
	 * @return
	 */
	boolean isException();

	/**
	 * 异常消息
	 * @return
	 */
	String getExceptionMessage();

	/**
	 * 异常的原因
	 * @return
	 */
	String getCause();
	
	/**
	 * 请求耗时 单位毫秒(ms)
	 * @return -1 无法计算耗时
	 */
	long use();

}
