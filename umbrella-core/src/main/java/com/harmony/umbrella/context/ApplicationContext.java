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

	protected static final int CREATE = 1;
	protected static final int INITIALIZED = 2;
	protected static final int DESTROY = 3;

	/**
	 * 初始化应用上下文
	 */
	public abstract void init();

	/**
	 * 销毁应用上下文
	 */
	public abstract void destory();

	/**
	 * 获取当前应用的应用上下文
	 * 
	 * @return 应用上下文
	 */
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

	/**
	 * 获取应用的jvm信息
	 * 
	 * @return jvm信息
	 */
	public JVMInformation getInforamtionOfJVM() {
		return ApplicationMetadata.jvmInfo;
	}

	/**
	 * 获取应用的服务信息
	 * 
	 * @return web服务器信息， 如果未初始化泽返回{@code null}
	 */
	public ServerInformation getInformationOfServer() {
		return serverInfo;
	}

	/**
	 * 获取应用的操作系统信息
	 * 
	 * @return 操作系统的信息
	 */
	public OSInformation getInformationOfOS() {
		return ApplicationMetadata.osInfo;
	}

	/**
	 * 获取应用的数据库信息
	 * 
	 * @return 数据库信息, 未初始化返回{@code null}
	 */
	public DBInformation getInformationOfDB() {
		return dbInfo;
	}

	/**
	 * 注册应用的web服务信息 <p> 一经注册就不在更改
	 * 
	 * @param servletContext
	 *            web上下文
	 */
	public void initializeServerInformation(ServletContext servletContext) {
		if (serverInfo == null) {
			serverInfo = ApplicationMetadata.INSTANCE.new ServerInformation(servletContext);
		}
	}

	/**
	 * 初始化应用的数据源信息<p>一经初始化就不在更改
	 * 
	 * @param conn
	 *            数据源的一个连接
	 * @param close
	 *            标识是否自动关闭数据源
	 * @throws SQLException
	 *             获取数据源信息失败
	 */
	public void initializeDBInformation(Connection conn, boolean close) throws SQLException {
		if (dbInfo == null) {
			try {
				dbInfo = ApplicationMetadata.INSTANCE.new DBInformation(conn);
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

}
