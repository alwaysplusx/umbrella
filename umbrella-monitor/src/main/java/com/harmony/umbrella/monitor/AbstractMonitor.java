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
package com.harmony.umbrella.monitor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监控基础抽象类
 * 
 * @author wuxii@foxmail.com
 * @param <T>
 *            监控的资源，由子类指定
 */
public abstract class AbstractMonitor<T> implements Monitor<T> {

	protected final static Object DEFAULT_RESOURCE_VALUE = new Object();

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractMonitor.class);

	/**
	 * 受到监控的模版名单
	 */
	protected Map<String, ResourceMatcher<T>> patternList = new ConcurrentHashMap<String, ResourceMatcher<T>>();

	/**
	 * 资源名单，综合{@link #getPolicy()}定性资源名单的意义
	 */
	protected Map<T, Object> resourceList = new ConcurrentHashMap<T, Object>();
	/**
	 * 监控策略
	 */
	protected MonitorPolicy policy = MonitorPolicy.WhiteList;

	/**
	 * 创建资源匹配器
	 * 
	 * @param pattern
	 * @return
	 */
	protected abstract ResourceMatcher<T> createMatcher(String pattern);

	@Override
	public MonitorPolicy getPolicy() {
		return policy;
	}

	@Override
	public void excludePattern(String pattern) {
		patternList.remove(pattern);
	}

	@Override
	public void includePattern(String pattern) {
		patternList.put(pattern, createMatcher(pattern));
	}

	@Override
	public void excludeResource(T resource) {
		resourceList.remove(resource);
	}

	@Override
	public void includeResource(T resource) {
		resourceList.put(resource, DEFAULT_RESOURCE_VALUE);
	}

	@Override
	public Set<T> getResources() {
		return Collections.unmodifiableSet(resourceList.keySet());
	}

	@Override
	public Set<String> getPatterns() {
		return Collections.unmodifiableSet(patternList.keySet());
	}

	@Override
	public boolean isMonitored(T resource) {
		switch (policy) {
		case Skip:
			return false;
		case All:
			return true;
		case BlockList:
			for (String pattern : patternList.keySet()) {
				ResourceMatcher<T> matcher = patternList.get(pattern);
				if (matcher.matches(resource) && resourceList.containsKey(resource)) {
					return true;
				}
			}
			return false;
		case WhiteList:
			for (String pattern : patternList.keySet()) {
				ResourceMatcher<T> matcher = patternList.get(pattern);
				if (matcher.matches(resource) && !resourceList.containsKey(resource)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

}
