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
package com.harmony.modules.scheduling.jmx;

import com.harmony.modules.scheduling.Scheduler;

/**
 * 发布JMX的辅助类
 * 
 * @author wuxii@foxmail.com
 * @see Scheduler
 *
 */
public class JMXSchedule implements JMXScheduleMBean {

	Scheduler schedule;

	public JMXSchedule() {
	}

	public JMXSchedule(Scheduler schedule) {
		this.schedule = schedule;
	}

	@Override
	public void restartAll() {
		schedule.restartAll();
	}

	@Override
	public void startAll() {
		schedule.startAll();
	}

	@Override
	public void removeAll() {
		schedule.removeAll();
	}

	@Override
	public void resumeAll() {
		schedule.resumeAll();
	}

	@Override
	public void pauseAll() {
		schedule.pauseAll();
	}

	@Override
	public void restart(String jobName) {
		this.schedule.restart(jobName);
	}

	@Override
	public void start(String jobName) {
		this.schedule.start(jobName);
	}

	@Override
	public void remove(String jobName) {
		this.schedule.remove(jobName);
	}

	@Override
	public void pause(String jobName) {
		this.schedule.pause(jobName);
	}

	@Override
	public void resume(String jobName) {
		this.schedule.resume(jobName);
	}

	@Override
	public String status(String jobName) {
		if (schedule.isStarted(jobName)) {
			return "job " + jobName + " is started";
		}
		return "not such job " + jobName;
	}

	public Scheduler getSchedule() {
		return schedule;
	}

	public void setSchedule(Scheduler schedule) {
		this.schedule = schedule;
	}

}
