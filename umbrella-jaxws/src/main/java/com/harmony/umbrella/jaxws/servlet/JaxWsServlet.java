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

import javax.jws.WebService;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.Constant;
import com.harmony.umbrella.io.util.ResourceScaner;
import com.harmony.umbrella.jaxws.JaxWsServerManager;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsServlet extends CXFNonSpringServlet {

    private static final long serialVersionUID = 1907515077730725429L;

    private static final Logger log = LoggerFactory.getLogger(JaxWsServlet.class);

    /**
     * 发布时候所扫视的包：默认值{@code com.harmony}
     */
    public static final String SCAN_PACKAGE = "scan-package";
    /**
     * webservice的url路径类型:annotation, class(default)
     */
    public static final String PATH_STYLE = "path-style";

    /**
     * 服务管理实例
     */
    private JaxWsServerManager serverManager = JaxWsServerManager.getInstance();

    /**
     * 资源扫描实例
     */
    private ResourceScaner scaner = ResourceScaner.getInstance();

    @Override
    public void init(ServletConfig sc) throws ServletException {
        super.init(sc);
        try {
            // TODO 从启动参数分割多个包进行扫描
            Class<?>[] classes = scaner.scanPackage(getScanPackage(sc), new ClassFilter() {
                @Override
                public boolean accept(Class<?> clazz) {
                    if (!ClassFilterFeature.NEWABLE.accept(clazz)) {
                        return false;
                    }
                    if (clazz.getAnnotation(WebService.class) == null)
                        return false;
                    return true;
                }
            });
            for (Class<?> clazz : classes) {
                serverManager.publish(clazz, buildPath(sc, clazz));
            }
        } catch (IOException e) {
            log.error("", e);
        } finally {
        }
    }

    /**
     * 根据web.xml中filter的启动参数path-style来创建webservice的访问url格式
     * 
     * <pre>
	 * &lt;servlet&gt;
	 *   &lt;servlet-name&gt;&lt;/servlet-name&gt;
	 *   &lt;servlet-class&gt;&lt;/servlet-class&gt;
	 *   &lt;init-param&gt;
	 *     &lt;param-name&gt;path-style&lt;/param-name&gt;
	 *     &lt;param-value&gt;annotation&lt;/param-value&gt;
	 *   &lt;/init-param&gt;
	 * &lt;/servlet&gt;
	 * </pre>
     */
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

    /**
     * 根据web.xml中filter的启动参数scan-package来设置扫描路径
     * 
     * <pre>
	 * &lt;servlet&gt;
	 *   &lt;servlet-name&gt;&lt;/servlet-name&gt;
	 *   &lt;servlet-class&gt;&lt;/servlet-class&gt;
	 *   &lt;init-param&gt;
	 *     &lt;param-name&gt;scan-package&lt;/param-name&gt;
	 *     &lt;param-value&gt;com.harmony&lt;/param-value&gt;
	 *   &lt;/init-param&gt;
	 * &lt;/servlet&gt;
	 * </pre>
     */
    protected String getScanPackage(ServletConfig sc) {
        return sc.getInitParameter(SCAN_PACKAGE) == null ? Constant.DEFAULT_PACKAGE : sc.getInitParameter(SCAN_PACKAGE);
    }

}
