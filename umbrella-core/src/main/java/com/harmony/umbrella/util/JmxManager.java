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
package com.harmony.umbrella.util;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuxii@foxmail.com
 */
public class JmxManager {

	public static final String DEFAULT_PREFIX = "org.moon.util.jmx";
	public static final String JMX_HTML_MBEAN_NAME = "org.moon.jmx:type=webConsole";
	private static final Logger log = LoggerFactory.getLogger(JmxManager.class);

	private static JmxManager instance;
	private final MBeanServer server;

	private Map<Object, String> mbeans = new HashMap<Object, String>();
	private Map<String, Integer> timesMap = new HashMap<String, Integer>();

	private JmxManager() {
		server = ManagementFactory.getPlatformMBeanServer();
	}

	public static JmxManager getInstance() {
		if (instance == null) {
			synchronized (JmxManager.class) {
				if (instance == null) {
					instance = new JmxManager();
				}
			}
		}
		return instance;
	}

	public MBeanServer getMBeanServer() {
		return server;
	}

	public boolean registerMBean(Object object) {
		return registerMBean(object, toJMXObjectName(object));
	}

	public boolean registerMBean(Object object, String objectName) {
		try {
			return registerMBean(object, new ObjectName(objectName));
		} catch (MalformedObjectNameException e) {
			log.error("error create object name,", e);
			return false;
		}
	}

	private boolean registerMBean(Object object, ObjectName objectName) {
		try {
			server.registerMBean(object, objectName);
			log.debug("register mbean {} successfully", objectName);
			return true;
		} catch (Exception e) {
			log.error("register mbean {} failed", objectName);
			log.error("", e);
		}
		return false;
	}

	public boolean unregisterMBean(Object object) {
		return unregisterMBean(toJMXObjectName(object));
	}

	public boolean unregisterMBean(String objectName) {
		ObjectName name;
		try {
			name = new ObjectName(objectName);
			if (server.isRegistered(name)) {
				server.unregisterMBean(name);
				log.debug("unregister mbean {} successfully", name);
				return true;
			}
		} catch (Exception e) {
			log.error("unregister mbean {} failed", objectName, e);
		}
		return false;
	}

	public boolean isRegistered(Object object) {
		String jmxObjectName = toJMXObjectName(object);
		return isRegistered(jmxObjectName);
	}

	public boolean isRegistered(String objectName) {
		try {
			return server.isRegistered(new ObjectName(objectName));
		} catch (MalformedObjectNameException e) {
			return false;
		}
	}

	public boolean registerHtmlAdaptorServer(int port) {
		ObjectName objectName;
		try {
			objectName = new ObjectName(JMX_HTML_MBEAN_NAME + "." + port);
			if (!server.isRegistered(objectName)) {
				try {
					Class<?> adaptorClass = Class.forName("com.sun.jdmk.comm.HtmlAdaptorServer");
					Constructor<?> constructor = adaptorClass.getConstructor(Integer.TYPE);
					Object object = constructor.newInstance(port);
					Method startMethod = adaptorClass.getMethod("start", new Class[] {});
					startMethod.invoke(object);
					try {
						server.registerMBean(object, objectName);
						log.debug("register html adaptor {} successfully", objectName);
						return true;
					} catch (Exception e) {
						log.debug("register html adaptor server failed", e);
					}
				} catch (Exception e) {
					log.error("{}", e);
				}
			}
		} catch (MalformedObjectNameException e) {
		}
		return false;
	}

	private String toJMXObjectName(Object object) {
		if (mbeans.containsKey(object)) {
			return mbeans.get(object);
		}
		StringBuffer sb = new StringBuffer();
		Package pkg = object.getClass().getPackage();
		if (pkg != null) {
			sb.append(pkg.getName());
		} else {
			sb.append(DEFAULT_PREFIX);
		}
		sb.append(":type=").append(object.getClass().getSimpleName());
		final String objectName = sb.toString();
		if (timesMap.containsKey(objectName)) {
			Integer times = timesMap.get(objectName);
			sb.append("_").append(times);
			timesMap.put(objectName, (times + 1));
		} else {
			timesMap.put(objectName, 0);
		}
		mbeans.put(object, sb.toString());
		return sb.toString();
	}

}
