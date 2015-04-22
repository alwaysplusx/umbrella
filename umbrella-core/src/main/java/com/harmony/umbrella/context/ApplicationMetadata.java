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

import static com.harmony.umbrella.util.PropUtils.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletContext;

/**
 * @author wuxii@foxmail.com
 */
class ApplicationMetadata {

	static final ApplicationMetadata INSTANCE = new ApplicationMetadata();
	static final JVMInformation jvmInfo = INSTANCE.new JVMInformation();
	static final OSInformation osInfo = INSTANCE.new OSInformation();

	static final DBInformation EMPTY_DATABASEINFO = INSTANCE.new DBInformation();

	class ServerInformation {

		public final String servletVersion;
		public final String serverName;
		public final int serverType;

		public ServerInformation(ServletContext context) {
			this.serverName = context.getServerInfo();
			this.servletVersion = context.getMajorVersion() + "." + context.getMinorVersion();
			this.serverType = serverType(serverName);
		}

		public static final int UNKNOW = -1;
		public static final int OTHERS = 0;
		public static final int WEBLOGIC = 1;
		public static final int WEBSPHERE = 2;
		public static final int GLASSFISH = 3;
		public static final int JBOSS = 4;
		public static final int TOMCAT = 5;

		private int serverType(String serverName) {
			serverName = serverName.toLowerCase();
			if (serverName.indexOf("weblogic") != -1) {
				return WEBLOGIC;
			} else if (serverName.indexOf("websphere") != -1) {
				return WEBSPHERE;
			} else if (serverName.indexOf("glassfish") != -1) {
				return GLASSFISH;
			} else if (serverName.indexOf("jboss") != -1) {
				return JBOSS;
			} else if (serverName.indexOf("tomcat") != -1) {
				return TOMCAT;
			}
			return OTHERS;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(serverName).append(" [servletVersion=").append(servletVersion).append("]");
			return sb.toString();
		}

	}

	class DBInformation {

		public final String productName;
		public final String productVersion;
		public final String url;
		public final String userName;
		public final String driverName;
		public final String driverVersion;
		public final int databaseType;

		public static final int UNKNOW = -1;
		public static final int OTHERS = 0;
		public static final int ORACLE = 1;
		public static final int MYSQL = 2;
		public static final int DB2 = 3;
		public static final int H2 = 4;
		public static final int HSQL = 5;
		public static final int SQLSERVER = 6;
		public static final int POSTGRESQL = 7;

		private DBInformation() {
			productName = productVersion = url = userName = driverName = driverVersion = "";
			databaseType = -1;
		}

		public DBInformation(Connection connection) throws SQLException {
			DatabaseMetaData dbmd = connection.getMetaData();
			this.productName = dbmd.getDatabaseProductName();
			this.productVersion = dbmd.getDatabaseProductVersion();
			this.url = dbmd.getURL();
			this.userName = dbmd.getUserName();
			this.driverName = dbmd.getDriverVersion();
			this.driverVersion = dbmd.getDriverVersion();
			this.databaseType = databaseType(productName);
		}

		private final int databaseType(String databaseName) {
			databaseName = databaseName.toLowerCase();
			if (databaseName == null) {
				return UNKNOW;
			} else if (databaseName.indexOf("oracle") != -1) {
				return ORACLE;
			} else if (databaseName.indexOf("postgresql") != -1) {
				return POSTGRESQL;
			} else if (databaseName.indexOf("db2") != -1) {
				return DB2;
			} else if (databaseName.indexOf("sql server") != -1) {
				return SQLSERVER;
			} else if (databaseName.indexOf("mysql") != -1) {
				return MYSQL;
			} else if (databaseName.indexOf("hsql") != -1) {
				return HSQL;
			} else if (databaseName.indexOf("h2") != -1) {
				return H2;
			}
			return OTHERS;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(productName).append("[versuib=").append(productVersion).append("]").append("\n").append(driverName).append("[").append(driverVersion).append("]");
			return sb.toString();
		}

	}

	class JVMInformation {

		public final String specificationName;
		public final String specificationVersion;
		public final String runtimeName;
		public final String runtimeVersion;
		public final String vmName;
		public final String vmVersion;
		public final String vmVendor;
		public final String classVersion;
		public final String libraryPath;
		public final String classPath;
		public final String javaVersion;
		public final String javaHome;
		public final String javaVendor;

		private JVMInformation() {
			this.specificationName = getSystemProperty("java.specification.name");
			this.specificationVersion = getSystemProperty("java.specification.version");
			this.runtimeName = getSystemProperty("java.runtime.name");
			this.runtimeVersion = getSystemProperty("java.runtime.version");
			this.vmName = getSystemProperty("java.vm.name");
			this.vmVendor = getSystemProperty("java.vm.vendor");
			this.vmVersion = getSystemProperty("java.vm.version");
			this.javaVersion = getSystemProperty("java.version");
			this.javaHome = getSystemProperty("java.home");
			this.javaVendor = getSystemProperty("java.vendor");
			this.classVersion = getSystemProperty("java.class.version");
			this.libraryPath = getSystemProperty("java.library.path");
			this.classPath = getSystemProperty("java.class.path");
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(javaVendor).append(" [").append(javaVersion).append("]\n").append(vmName).append(" [").append(vmVersion).append("]\n").append(specificationName).append(" ")
					.append(specificationVersion);
			return sb.toString();
		}

	}

	class OSInformation {

		public final String osName;
		public final String osVersion;
		public final String osPatchLevel;
		public final String userHome;
		public final String userName;
		public final String language;
		public final String timeZone;
		public final String fileEncoding;
		public final String fileSeparator;
		public final String cpu;
		public final String cpuModel;

		private OSInformation() {
			this.osName = getSystemProperty("os.name");
			this.osVersion = getSystemProperty("os.version");
			this.osPatchLevel = getSystemProperty("sun.os.patch.level");
			this.userHome = getSystemProperty("user.home");
			this.userName = getSystemProperty("user.name");
			this.language = getSystemProperty("user.language");
			this.timeZone = getSystemProperty("user.timezone");
			this.fileEncoding = getSystemProperty("file.encoding");
			this.fileSeparator = getSystemProperty("file.separator");
			this.cpu = getSystemProperty("sun.cpu.isalist");
			this.cpuModel = getSystemProperty("sun.arch.data.model");
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(osName).append(" [version=").append(osVersion).append(", cpu=").append(cpu).append("]");
			return sb.toString();
		}

	}

}
