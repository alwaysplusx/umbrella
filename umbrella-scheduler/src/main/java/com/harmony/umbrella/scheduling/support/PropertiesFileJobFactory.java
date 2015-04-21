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
package com.harmony.umbrella.scheduling.support;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Set;

import com.harmony.umbrella.core.BeanLoader;
import com.harmony.umbrella.core.ClassBeanLoader;
import com.harmony.umbrella.scheduling.Job;
import com.harmony.umbrella.scheduling.JobFactory;
import com.harmony.umbrella.scheduling.Trigger;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PropertiesFileJobFactory implements JobFactory {

	public static final String jobPropertiesFileLocation = "META-INF/scheduler/jobs.properties";

	public static final String triggerPropertiesFileLocation = "META-INF/scheduler/triggers.properties";

	private BeanLoader beanLoader = new ClassBeanLoader();
	private final String jobFileLocation;
	private final String triggerFileLocation;

	public PropertiesFileJobFactory() {
		this(jobPropertiesFileLocation, triggerPropertiesFileLocation);
	}

	public PropertiesFileJobFactory(String jobFileLocation, String triggerFileLocation) {
		this.jobFileLocation = jobFileLocation;
		this.triggerFileLocation = triggerFileLocation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Job> getJobClass(String jobName) {
		try {
			Properties props = PropUtils.loadProperties(jobFileLocation);
			String jobClassName = props.getProperty(jobName);
			if (jobClassName != null) {
				Class<?> clazz = Class.forName(jobClassName);
				if (Job.class.isAssignableFrom(clazz) && !clazz.isInterface() && Modifier.isPublic(clazz.getModifiers())) {
					return (Class<? extends Job>) clazz;
				}
			} else {
				throw new IllegalArgumentException(jobClassName + "class not " + Job.class.getName() + " sub class");
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("job class not find");
		}
		return null;
	}

	@Override
	public Trigger getJobTrigger(String jobName) {
		try {
			Properties props = PropUtils.loadProperties(triggerFileLocation);
			String expression = props.getProperty(jobName);
			if (expression != null)
				return new ExpressionTrigger(expression);
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public Job getJob(String jobName) {
		Class<? extends Job> jobClass = getJobClass(jobName);
		if (jobClass != null) {
			return beanLoader.loadBean(jobClass);
		}
		return null;
	}

	public void setBeanLoader(BeanLoader beanLoader) {
		Assert.notNull(beanLoader, "bean loader must not be null");
		this.beanLoader = beanLoader;
	}

	@Override
	public Set<String> getAllJobNames() {
		Properties props = null;
		try {
			props = PropUtils.loadProperties(jobFileLocation);
		} catch (IOException e) {
		}
		return props.stringPropertyNames();
	}
}
