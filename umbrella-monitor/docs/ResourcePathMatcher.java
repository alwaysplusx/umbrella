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
package com.harmony.umbrella.monitor.matcher;

import com.harmony.umbrella.io.util.AntPathMatcher;
import com.harmony.umbrella.io.util.PathMatcher;
import com.harmony.umbrella.monitor.ResourceMatcher;

/**
 * http 资源连接匹配
 * 
 * @author wuxii@foxmail.com
 * @see PathMatcher
 * @see AntPathMatcher
 */
public class ResourcePathMatcher implements ResourceMatcher<String> {

    private final String pattern;
    private PathMatcher matcher;

    public ResourcePathMatcher(String pattern) {
        this.pattern = pattern;
        this.matcher = new AntPathMatcher(pattern);
    }

    @Override
    public boolean matches(String resource) {
        return matcher.isPattern(resource);
    }

    @Override
    public String getExpression() {
        return pattern;
    }

    public PathMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(PathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public String toString() {
        return "resource matcher of pattern:" + pattern;
    }
}
