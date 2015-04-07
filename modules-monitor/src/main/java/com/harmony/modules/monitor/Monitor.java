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

/**
 * 监视器，默认监视所有资源
 * @author wuxii@foxmail.com
 */
public interface Monitor {

	/**
	 * 排除在监视名单外
	 * @param resource 资源名称
	 */
	void exclude(String resource);

	/**
	 * 包含在监视名单内
	 * @param resource 资源名称
	 */
	void include(String resource);

	/**
	 * 检测资源是否收到监视
	 * @param resource 资源名称
	 * @return 
	 */
	boolean isMonitored(String resource);

	/**
	 * 监视名单
	 * @return
	 */
	String[] getMonitorList();

	/**
	 * 是否为白名单策略
	 * <p>是否开启白名单策略，开启后只拦截在监视名单中的资源
	 * @return
	 */
	boolean isUseWhiteList();

	/**
	 * 设置开启或关闭白名单策略
	 * @param use
	 */
	void useWhiteList(boolean use);

}
