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
package com.harmony.umbrella.ws.servlet;

import javax.jws.WebService;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.Path;

import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.jaxrs.JaxRsServerManager;
import com.harmony.umbrella.ws.jaxws.JaxWsServerManager;

/**
 * @author wuxii@foxmail.com
 */
public class WebServiceServlet extends CXFNonSpringServlet {

    private static final long serialVersionUID = 1907515077730725429L;

    private static final Log log = Logs.getLog(WebServiceServlet.class);

    /**
     * 根据web.xml中filter的启动参数scan-package来设置扫描路径
     * <p>
     * 发布时候所扫视的包：默认值{@code com.harmony}
     * 
     * <pre>
     * &lt;servlet&gt;
     *   &lt;servlet-name&gt;wsServlet&lt;/servlet-name&gt;
     *   &lt;servlet-class&gt;com.harmony.umbrella.ws.servlet.WebServiceServlet&lt;/servlet-class&gt;
     *   &lt;init-param&gt;
     *     &lt;param-name&gt;scan-package&lt;/param-name&gt;
     *     &lt;param-value&gt;com.harmony&lt;/param-value&gt;
     *   &lt;/init-param&gt;
     * &lt;/servlet&gt;
     * </pre>
     */
    public static final String SCAN_PACKAGE = "scan-package";

    /**
     * 根据web.xml中filter的启动参数path-style来创建webservice的访问url格式
     * <p>
     * webservice的url路径类型:annotation, class(default)
     * 
     * <pre>
     * &lt;servlet&gt;
     *   &lt;servlet-name&gt;wsServlet&lt;/servlet-name&gt;
     *   &lt;servlet-class&gt;com.harmony.umbrella.ws.servlet.WebServiceServlet&lt;/servlet-class&gt;
     *   &lt;init-param&gt;
     *     &lt;param-name&gt;path-style&lt;/param-name&gt;
     *     &lt;param-value&gt;annotation&lt;/param-value&gt;
     *   &lt;/init-param&gt;
     * &lt;/servlet&gt;
     * </pre>
     */
    public static final String PATH_STYLE = "path-style";

    private ResourceManager resourceManager = ResourceManager.getInstance();

    private String pathStyle;

    private String scanPackages;

    private ServletConfig servletConfig;

    private JaxWsServerManager jaxWsServerManager = JaxWsServerManager.getInstance();

    private JaxRsServerManager jaxRsServerManager = JaxRsServerManager.getInstance();

    @Override
    public void init(final ServletConfig sc) throws ServletException {
        super.init(sc);
        this.servletConfig = sc;
        this.pathStyle = getInitParameter(PATH_STYLE, "class").toLowerCase();
        this.scanPackages = getScanPackages();
        this.resourceManager.getClasses(scanPackages, new ClassFilter() {

            @Override
            public boolean accept(Class<?> clazz) {
                if (!ClassFilterFeature.NEWABLE.accept(clazz)) {
                    return false;
                }

                WebService wsAnn = clazz.getAnnotation(WebService.class);
                if (wsAnn != null) {
                    // publish jaxws service in filter
                    String address = isAnnotationPathStyle() ? pathOfAnnotation(clazz, wsAnn) : pathOfClass(clazz);
                    try {
                        jaxWsServerManager.publish(clazz, address);
                        log.info("publish jaxws service {} at {}", clazz.getName(), address);
                    } catch (Exception e) {
                        log.warn("can't publish service {} at {}", clazz.getName(), address, e);
                        return false;
                    }
                }

                Path pathAnn = clazz.getAnnotation(Path.class);
                if (pathAnn != null) {
                    // publish jaxrs service in filter
                    String address = isAnnotationPathStyle() ? pathOfAnnotation(clazz, pathAnn) : pathOfClass(clazz);
                    try {
                        jaxRsServerManager.publish(clazz, address);
                        log.info("publish jaxrs service {} at {}", clazz.getName(), address);
                    } catch (Exception e) {
                        log.warn("can't publish service {} at {}", clazz.getName(), address, e);
                        return false;
                    }
                }

                return true;
            }
        });
    }

    protected boolean isClassPathStyle() {
        return "class".equals(pathStyle);
    }

    protected boolean isAnnotationPathStyle() {
        return "annotation".equals(pathStyle);
    }

    public String pathOfClass(Class<?> clazz) {
        return "/" + clazz.getSimpleName();
    }

    public String pathOfAnnotation(Class<?> clazz, Path ann) {
        String path = ann.value();
        if (StringUtils.isBlank(path)) {
            return pathOfClass(clazz);
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    public String pathOfAnnotation(Class<?> clazz, WebService ann) {
        String serviceName = ann.serviceName();
        if (StringUtils.isBlank(serviceName)) {
            return pathOfClass(clazz);
        }
        return serviceName.startsWith("/") ? serviceName : "/" + serviceName;
    }

    protected String getScanPackages() {
        return getInitParameter(SCAN_PACKAGE, ApplicationContext.APPLICATION_PACKAGE);
    }

    public String getInitParameter(String name, String defaultValue) {
        String value = servletConfig.getInitParameter(name);
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value;
    }

}
