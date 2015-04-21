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
package com.harmony.umbrella.scheduling;

import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public interface JobFactory {

	/**
	 * 根据job的名称获取对应的job类
	 * 
	 * @param jobName
	 *            job名称
	 * @return job class
	 */
	Class<? extends Job> getJobClass(String jobName);

	/**
	 * 根据job的名称获取job的触发规则
	 * 
	 * @param jobName
	 *            job名称
	 * @return job触发贵族
	 */
	Trigger getJobTrigger(String jobName);

	/**
	 * 根据job的名称获取对应的job实例
	 * 
	 * @param jobName
	 *            job名称
	 * @return job instance
	 */
	Job getJob(String jobName);

	/**
	 * 获得全部的job的名称
	 * 
	 * @return
	 */
	Set<String> getAllJobNames();

}
