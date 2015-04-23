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
package com.harmony.umbrella.monitor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.monitor.support.AbstractHttpMonitor;

/**
 * Http Filter 实现监控http请求
 * 
 * @author wuxii@foxmail.com
 */
public class HttpMonitorFilter extends AbstractHttpMonitor {

    private static final Logger log = LoggerFactory.getLogger(HttpMonitorFilter.class);

    public HttpMonitorFilter() {
        this(DEFAULT_PATH_PATTERN);
    }

    public HttpMonitorFilter(String pattern) {
        this.includePattern(pattern);
    }

    @Override
    protected void persistGraph(HttpGraph graph) {
        log.info("http monitor graph:{}", graph);
    }

}