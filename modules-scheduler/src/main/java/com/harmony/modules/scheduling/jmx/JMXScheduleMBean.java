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

/**
 * 发布JMS MBean接口
 * 
 * @author wuxii@foxmail.com
 */
public interface JMXScheduleMBean {

	void restartAll();

	void startAll();

	void removeAll();

	void resumeAll();

	void pauseAll();

	void restart(String jobName);

	void start(String jobName);

	void remove(String jobName);

	void pause(String jobName);

	void resume(String jobName);

	String status(String jobName);

}
