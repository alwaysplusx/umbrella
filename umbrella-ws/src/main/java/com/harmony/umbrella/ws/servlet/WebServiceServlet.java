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
import com.harmony.umbrella.io.util.ResourceScaner;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.ws.ServerManager;

/**
 * @author wuxii@foxmail.com
 */
public class WebServiceServlet extends CXFNonSpringServlet {

    private static final long serialVersionUID = 1907515077730725429L;

    private static final Logger log = LoggerFactory.getLogger(WebServiceServlet.class);

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
    private ServerManager serverManager = ServerManager.getServerManager();

    /**
     * 资源扫描实例
     */
    private ResourceScaner scaner = ResourceScaner.getInstance();

    @Override
    public void init(final ServletConfig sc) throws ServletException {
        super.init(sc);

        ClassFilter filter = new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                if (!ClassFilterFeature.NEWABLE.accept(clazz)) {
                    return false;
                }
                if (clazz.getAnnotation(WebService.class) == null)
                    return false;
                // do publish in filter
                log.info("publish service {}", clazz.getName());
                serverManager.publish(clazz, buildServicePath(sc, clazz));
                return true;
            }
        };

        String[] packages = getPackages(sc);
        for (String pkg : packages) {
            scaner.scanPackage(pkg, filter);
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
    private String buildServicePath(ServletConfig sc, Class<?> c) {
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
    protected String[] getPackages(ServletConfig sc) {
        String pkgs = sc.getInitParameter(SCAN_PACKAGE);
        if (pkgs == null) {
            return new String[] { Constants.DEFAULT_PACKAGE };
        }
        return pkgs.split(",");
    }

}
