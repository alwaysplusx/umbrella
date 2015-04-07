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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.harmony.modules.utils.PropUtils;
import com.harmony.modules.utils.StringUtils;

/**
 * 加载属性文件中的job，启动定时任务
 * @author wuxii@foxmail.com
 */
public abstract class AbstractPropertiesFileScheduler implements Scheduler {

	public static final String jobsPropertiesFileLocation = "META-INF/scheduler/jobs.properties";
	private Map<String, Status> jobStatusMap = new HashMap<String, Status>();
	protected Map<String, Class<? extends Job>> jobClassMap = new HashMap<String, Class<? extends Job>>();
	private final String jobsPropertiesFile;

	public AbstractPropertiesFileScheduler() throws IOException {
		this(jobsPropertiesFileLocation);
	}

	public AbstractPropertiesFileScheduler(String jobsPropertiesFile) throws IOException {
		this.jobsPropertiesFile = jobsPropertiesFile;
		init();
	}

	protected abstract void doStart(String jobName, Class<? extends Job> jobClass);

	protected abstract void doStop(String jobName, Class<? extends Job> jobClass);

	protected abstract void doPause(String jobName, Class<? extends Job> jobClass);

	protected abstract void doResume(String jobName, Class<? extends Job> jobClass);

	public abstract JobInfo getJobInfo(String jobName);

	@Override
	public void start(String jobName) {
		if (hasJob(jobName) && !isStarted(jobName)) {
			doStart(jobName, jobClassMap.get(jobName));
		}
	}

	@Override
	public void stop(String jobName) {
		if (hasJob(jobName) && !isStopped(jobName)) {
			doStop(jobName, jobClassMap.get(jobName));
		}
	}

	@Override
	public void pause(String jobName) {
		if (hasJob(jobName) && !isPaused(jobName)) {
			doPause(jobName, jobClassMap.get(jobName));
		}
	}

	@Override
	public void resume(String jobName) {
		if (hasJob(jobName) && isPaused(jobName)) {
			doResume(jobName, jobClassMap.get(jobName));
		}
	}

	@SuppressWarnings("unchecked")
	private void init() throws IOException {
		Properties props = PropUtils.loadProperties(jobsPropertiesFile);
		for (String name : props.stringPropertyNames()) {
			String jobClassName = props.getProperty(name);
			if (StringUtils.isEmpty(jobClassName)) {
				throw new IllegalArgumentException("job[" + name + "] class cannot be null");
			}
			try {
				Class<?> jobClass = Class.forName(jobClassName);
				if (!jobClass.isInterface() && Job.class.isAssignableFrom(jobClass)) {
					jobClassMap.put(name, (Class<? extends Job>) jobClass);
					jobStatusMap.put(name, Status.READY);
					continue;
				}
				throw new IllegalArgumentException("job class [" + jobClass + "] not subclass of " + Job.class);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("job[" + name + "] class not find", e);
			}

		}
	}

	@Override
	public void restart(String jobName) {
		stop(jobName);
		start(jobName);
	}

	@Override
	public void restartAll() {
		stopAll();
		startAll();
	}

	@Override
	public void startAll() {
		for (String jobName : getJobNames()) {
			start(jobName);
		}
	}

	@Override
	public void stopAll() {
		for (String jobName : getJobNames()) {
			stop(jobName);
		}
	}

	@Override
	public void resumeAll() {
		for (String jobName : getJobNames()) {
			resume(jobName);
		}
	}

	@Override
	public void pauseAll() {
		for (String jobName : getJobNames()) {
			pause(jobName);
		}
	}

	public String[] getJobNames() {
		Set<String> names = jobClassMap.keySet();
		return names.toArray(new String[names.size()]);
	}

	@Override
	public boolean isStarted(String jobName) {
		return Status.START.equals(jobStatusMap.get(jobName));
	}

	@Override
	public boolean isStopped(String jobName) {
		return Status.STOP.equals(jobStatusMap.get(jobName));
	}

	@Override
	public boolean isPaused(String jobName) {
		return Status.PAUSE.equals(jobStatusMap.get(jobName));
	}

	public Status getJobStatus(String jobName) {
		return jobStatusMap.get(jobName);
	}

	@Override
	public boolean hasJob(String jobName) {
		return jobStatusMap.containsKey(jobName);
	}

	public enum Status {
		READY, START, PAUSE, STOP;
	}
}
