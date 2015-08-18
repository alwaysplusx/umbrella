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
package com.harmony.umbrella.monitor.support;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.monitor.AbstractMonitor;
import com.harmony.umbrella.monitor.HttpMonitor;
import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.monitor.matcher.UrlPathMatcher;
import com.harmony.umbrella.monitor.util.MonitorUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 基于Http监控的Filter
 * 
 * @author wuxii@foxmail.com
 * @see javax.servlet.Filter
 */
public abstract class AbstractHttpMonitorFilter extends AbstractMonitor<String> implements HttpMonitor {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractHttpMonitorFilter.class);

    private ResourceMatcher<String> resourceMatcher;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String resourceId = getRequestId(request);
        if (StringUtils.isBlank(resourceId) || !isMonitored(resourceId)) {
            chain.doFilter(req, resp);
        } else {
            aroundMonitor(resourceId, request, response, chain);
        }
    }

    protected String getRequestId(HttpServletRequest request) {
        return MonitorUtils.requestId(request);
    }

    protected abstract void aroundMonitor(String resourceId, HttpServletRequest req, HttpServletResponse resp, FilterChain chain);

    public void setResourceMatcher(ResourceMatcher<String> resourceMatcher) {
        this.resourceMatcher = resourceMatcher;
    }

    @Override
    protected ResourceMatcher<String> getResourceMatcher() {
        if (resourceMatcher == null) {
            resourceMatcher = new UrlPathMatcher();
        }
        return resourceMatcher;
    }

    @Override
    public void destroy() {
        this.cleanAll();
    }

}
