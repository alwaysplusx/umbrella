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
package com.harmony.modules.scheduling;

import com.harmony.modules.scheduling.Scheduler.JobInfo;

/**
 * job类,所有的定时任务实现这个类
 * 
 * @author wuxii@foxmail.com
 *
 */
public interface Job {

	/**
	 * 执行job,定时器会定时调用该方法
	 * 
	 * @param jobInfo
	 *            当前定时器的运行情况
	 */
	void process(JobInfo jobInfo);

}
