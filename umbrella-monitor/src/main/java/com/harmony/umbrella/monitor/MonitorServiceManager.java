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
package com.harmony.umbrella.monitor;

import java.util.Set;

import com.harmony.umbrella.monitor.Monitor;

/**
 * @author wuxii@foxmail.com
 */
public class MonitorServiceManager {

    private MonitorServiceManager() {

    }

    public static MonitorServiceManager getInstance() {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public void addMonitorService(Monitor monitor) {

    }

    public Set<String> getPatterns() {
        return null;
    }

    public Set<String> getResources() {
        return null;
    }

    public boolean addPattern(String pattern) {
        return false;
    }

    public boolean addResource(String resource) {
        return false;
    }

    public boolean containsPattern(String pattern) {
        return false;
    }

    public boolean containsResource(String resource) {
        return false;
    }
}
