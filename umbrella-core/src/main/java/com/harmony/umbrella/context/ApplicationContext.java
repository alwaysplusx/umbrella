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
package com.harmony.umbrella.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ApplicationMetadata.DBInformation;
import com.harmony.umbrella.context.ApplicationMetadata.JVMInformation;
import com.harmony.umbrella.context.ApplicationMetadata.OSInformation;
import com.harmony.umbrella.context.ApplicationMetadata.ServerInformation;
import com.harmony.umbrella.context.spi.ApplicationContextProvider;
import com.harmony.umbrella.core.BeanFactory;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

	protected static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);
	private static final ServiceLoader<ApplicationContextProvider> providers = ServiceLoader.load(ApplicationContextProvider.class);
	private static ServerInformation serverInfo;
	private static DBInformation dbInfo;

	public abstract void init();

	public abstract void destory();

	public static final ApplicationContext getApplicationContext() {
		ApplicationContext context = null;
		providers.reload();
		for (ApplicationContextProvider provider : providers) {
			try {
				context = provider.createApplicationContext();
			} catch (Exception e) {
				LOG.error("", e);
			}
		}
		if (context == null) {
			throw new ApplicationContextException("can't find any application context privider to create context");
		}
		return context;
	}

	public JVMInformation getInforamtionOfJVM() {
		return ApplicationMetadata.jvmInfo;
	}

	public ServerInformation getInformationOfServer() {
		return serverInfo;
	}

	public DBInformation getInformationOfDB() {
		return dbInfo;
	}

	public void initializeServerInformation(ServletContext servletContext) {
		if (serverInfo == null) {
			serverInfo = ApplicationMetadata.INSTANCE.new ServerInformation(servletContext);
		}
	}

	public void initializeDBInformation(Connection conn, boolean close) {
		if (dbInfo == null) {
			try {
				dbInfo = ApplicationMetadata.INSTANCE.new DBInformation(conn);
			} catch (SQLException e) {
			} finally {
				if (close) {
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
			}
		}
	}

	public OSInformation getInformationOfOS() {
		return ApplicationMetadata.osInfo;
	}

}
