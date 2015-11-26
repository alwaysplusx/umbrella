/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.web.support;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.context.DefaultHttpCurrentContext;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.web.util.FrontUtils;

/**
 * @author wuxii@foxmail.com
 */
public class CurrentContextFilter implements Filter {

    public static final Set<String> DEFAULT_EXCLUDE_EXTENSION;

    static {
        Set<String> extensions = new HashSet<String>();
        extensions.add(".js");
        extensions.add(".jpg");
        extensions.add(".png");
        extensions.add(".gif");
        extensions.add(".gif");
        extensions.add(".html");
        extensions.add(".htm");
        DEFAULT_EXCLUDE_EXTENSION = Collections.unmodifiableSet(extensions);
    }

    private ApplicationContext context;

    private final Set<String> excludeExtension = new HashSet<String>(DEFAULT_EXCLUDE_EXTENSION);

    private Set<String> excludeUrls = new HashSet<String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = ApplicationContext.getApplicationContext();
        String urls = filterConfig.getInitParameter("exclude-url");
        if (StringUtils.isNotBlank(urls)) {
            for (String url : urls.split(",")) {
                excludeUrls.add(url.trim());
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestUrl = FrontUtils.getRequestUrl((HttpServletRequest) request);

        // 业务相关的链接才设置应用环境
        if (isBusinessUrl(requestUrl)) {

            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;

            CurrentContext occ = context.getCurrentContext();

            try {
                context.setCurrentContext(new DefaultHttpCurrentContext(req, resp));
                chain.doFilter(request, response);
            } finally {
                context.setCurrentContext(occ);
            }

        } else {
            chain.doFilter(request, response);
        }

    }

    private boolean isBusinessUrl(String url) {
        if (excludeUrls.contains(url)) {
            return false;
        }
        for (String extension : excludeExtension) {
            if (url.endsWith(extension)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void destroy() {
    }

}
