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
package com.harmony.umbrella.jaxws.servlet;

import java.io.IOException;
import java.lang.reflect.Modifier;

import javax.jws.WebService;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.ClassFilter;
import com.harmony.umbrella.io.utils.ResourceScaner;
import com.harmony.umbrella.jaxws.JaxWsServerManager;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsServlet extends CXFNonSpringServlet {

	private static final long serialVersionUID = 1907515077730725429L;
	public static final String SCAN_PACKAGE = "scan-package";
	public static final String PATH_STYLE = "path-style";

	private static final Logger log = LoggerFactory.getLogger(JaxWsServlet.class);
	private JaxWsServerManager jaxWsManager = JaxWsServerManager.getInstance();
	private ResourceScaner scaner = ResourceScaner.getInstance();
	private BeanFactory beanFactory;

	@Override
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc);
		BeanFactory loader = jaxWsManager.getBeanLoader();
		try {
			Class<?>[] classes = scaner.scanPackage(getScanPackage(sc), new ClassFilter() {
				@Override
				public boolean accept(Class<?> clazz) {
					if (clazz.isInterface())
						return false;
					if (clazz.getModifiers() != Modifier.PUBLIC)
						return false;
					if (clazz.getModifiers() == Modifier.ABSTRACT)
						return false;
					if (clazz.isMemberClass())
						return false;
					if (clazz.isEnum())
						return false;
					if (clazz.isAnnotation())
						return false;
					if (clazz.isAnonymousClass())
						return false;
					if (clazz.getAnnotation(WebService.class) == null)
						return false;
					return true;
				}
			});
			jaxWsManager.setBeanLoader(beanFactory == null ? loader : beanFactory);
			for (Class<?> clazz : classes) {
				jaxWsManager.publish(clazz, buildPath(sc, clazz));
			}
		} catch (IOException e) {
			log.error("", e);
		} finally {
			jaxWsManager.setBeanLoader(loader);
		}
	}

	private String buildPath(ServletConfig sc, Class<?> c) {
		String pathStyle = sc.getInitParameter(PATH_STYLE);
		if (pathStyle != null && "annotation".endsWith(pathStyle)) {
			return getPathFromAnnotation(c);
		}
		return getPathFromClass(c);
	}

	private String getPathFromClass(Class<?> cls) {
		return "/" + cls.getSimpleName();
	}

	private String getPathFromAnnotation(Class<?> cls) {
		WebService webService = cls.getAnnotation(WebService.class);
		if (webService != null) {
			return "".equals(webService.serviceName()) ? getPathFromClass(cls) : "/" + webService.serviceName();
		}
		return getPathFromClass(cls);
	}

	protected String getScanPackage(ServletConfig sc) {
		return sc.getInitParameter(SCAN_PACKAGE) == null ? "com.harmony" : sc.getInitParameter(SCAN_PACKAGE);
	}

	public BeanFactory getBeanLoader() {
		return beanFactory;
	}

	public void setBeanLoader(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
