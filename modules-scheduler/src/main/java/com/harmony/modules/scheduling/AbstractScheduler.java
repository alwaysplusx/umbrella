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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.harmony.modules.utils.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractScheduler implements Scheduler {

	public static final String jobsProperties = "jobs.properties";

	protected Map<String, Status> jobsMap = new HashMap<String, Status>();
	private Properties jobClassProps = new Properties();

	@Override
	public void restart(String jobName) {
		remove(jobName);
		start(jobName);
	}

	@Override
	public void start(String jobName) {
		Class<? extends Job> jobClass = getJobClass(jobName);
		Trigger trigger = getJobTrigger(jobName);
		startJob(jobName, jobClass, trigger);
	}

	protected abstract void startJob(String jobName, Class<? extends Job> jobClass, Trigger trigger);

	protected abstract Trigger getJobTrigger(String jobName);

	@Override
	public void restartAll() {
		removeAll();
		startAll();
	}

	@Override
	public void startAll() {
		List<String> jobNames = getAllJobName();
		for (String jobName : jobNames) {
			start(jobName);
			jobsMap.put(jobName, Status.start);
		}
	}

	protected abstract List<String> getAllJobName();

	protected Job newJob(Class<? extends Job> jobClass) {
		try {
			return jobClass.newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}

	@Override
	public void removeAll() {
		Iterator<String> iterator = jobsMap.keySet().iterator();
		while (iterator.hasNext()) {
			String jobName = iterator.next();
			remove(jobName);
			jobsMap.remove(jobName);
		}
		jobsMap.clear();
	}

	@Override
	public void pauseAll() {
		Iterator<String> iterator = jobsMap.keySet().iterator();
		while (iterator.hasNext()) {
			String jobName = iterator.next();
			if (jobsMap.get(jobName) != Status.pause)
				continue;
			pause(jobName);
			jobsMap.put(jobName, Status.pause);
		}
	}

	@Override
	public void resumeAll() {
		Iterator<String> iterator = jobsMap.keySet().iterator();
		while (iterator.hasNext()) {
			String jobName = iterator.next();
			if (jobsMap.get(jobName) == Status.pause)
				continue;
			pause(jobName);
			jobsMap.put(jobName, Status.start);
		}
	}

	@Override
	public boolean isStarted(String jobName) {
		return jobsMap.containsKey(jobName) ? jobsMap.get(jobName) == Status.start : false;
	}

	@Override
	public boolean hasJob(String jobName) {
		return jobsMap.containsKey(jobName);
	}

	@SuppressWarnings("unchecked")
	protected Class<? extends Job> getJobClass(String jobName) {
		if (jobClassProps.isEmpty()) {
			try {
				jobClassProps = PropUtils.loadProperties(jobsProperties);
			} catch (IOException e) {
				throw new SchedulerException(e);
			}
		}
		if (jobClassProps.containsKey(jobName)) {
			String jobClassName = jobClassProps.getProperty(jobName);
			try {
				Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(jobClassName);
				return jobClass;
			} catch (ClassNotFoundException e) {
				throw new SchedulerException(e);
			}
		}
		throw new SchedulerException("job[" + jobName + "] class cannot be null");
	}

	public enum Status {
		start, pause;
	}

}
