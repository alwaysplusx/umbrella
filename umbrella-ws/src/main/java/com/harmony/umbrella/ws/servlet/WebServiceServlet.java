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

import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.Constants;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.ServerManager;

/**
 * @author wuxii@foxmail.com
 */
public class WebServiceServlet extends CXFNonSpringServlet {

    private static final long serialVersionUID = 1907515077730725429L;

    private static final Logger log = LoggerFactory.getLogger(WebServiceServlet.class);

    /**
     * 根据web.xml中filter的启动参数scan-package来设置扫描路径
     * <p>
     * 发布时候所扫视的包：默认值{@code com.harmony}
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
    public static final String SCAN_PACKAGE = "scan-package";

    /**
     * 根据web.xml中filter的启动参数path-style来创建webservice的访问url格式
     * <p>
     * webservice的url路径类型:annotation, class(default)
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
    public static final String PATH_STYLE = "path-style";

    /**
     * 服务管理实例
     */
    private ServerManager serverManager = ServerManager.getServerManager();

    private ResourceManager resourceManager = ResourceManager.getInstance();
    private String pathStyle;

    private String[] scanPackages;

    private ServletConfig servletConfig;

    @Override
    public void init(final ServletConfig sc) throws ServletException {
        super.init(sc);
        this.servletConfig = sc;
        this.pathStyle = getInitParameter(PATH_STYLE, "class").toLowerCase();
        this.scanPackages = getScanPackages();
        this.resourceManager.getClasses(scanPackages, new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                WebService ann = clazz.getAnnotation(WebService.class);
                if (ann == null || !ClassFilterFeature.NEWABLE.accept(clazz)) {
                    return false;
                }
                try {
                    // do publish in filter
                    String address = isAnnotationPathStyle() ? pathOfAnnotation(clazz, ann) : pathOfClass(clazz);
                    log.info("publish service {} at {}", clazz.getName(), address);
                    serverManager.publish(clazz, address);
                } catch (Exception e) {
                    return false;
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

    public String pathOfAnnotation(Class<?> clazz, WebService ann) {
        String serviceName = ann.serviceName();
        if (StringUtils.isBlank(serviceName)) {
            return pathOfClass(clazz);
        }
        return serviceName.startsWith("/") ? serviceName : "/" + serviceName;
    }

    protected String[] getScanPackages() {
        return getInitParameter(SCAN_PACKAGE, Constants.DEFAULT_PACKAGE).split(",");
    }

    public String getInitParameter(String name, String defaultValue) {
        String value = servletConfig.getInitParameter(name);
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value;
    }

}
