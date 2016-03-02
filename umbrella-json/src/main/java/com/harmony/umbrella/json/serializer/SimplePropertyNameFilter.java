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
package com.harmony.umbrella.json.serializer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.util.AntPathMatcher;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.PathMatcher;
import com.harmony.umbrella.util.StringUtils;

/**
 * 对需要json格式化的对象进行字段的过滤，默认模式为exclude(excludeMode = true)
 * 
 * @author wuxii@foxmail.com
 */
public class SimplePropertyNameFilter extends PropertyNameFilter {

    private static final Log log = Logs.getLog(SimplePropertyNameFilter.class);

    private PathMatcher matcher;

    /**
     * 过滤的模式
     */
    private boolean excludeMode;

    /**
     * 过滤的模版
     */
    private final Set<String> patterns = new HashSet<String>();

    public SimplePropertyNameFilter(Set<String> patterns, boolean excludeMode) {
        this(new AntPathMatcher(), patterns, excludeMode);
    }

    public SimplePropertyNameFilter(String... patterns) {
        this(new HashSet<String>(Arrays.asList(patterns)), true);
    }

    public SimplePropertyNameFilter(PathMatcher matcher, Set<String> patterns, boolean excludeMode) {
        Assert.notNull(matcher, "matcher must not be null");
        this.matcher = matcher;
        this.excludeMode = excludeMode;
        for (String property : patterns) {
            if (StringUtils.isNotBlank(property)) {
                this.patterns.add(property);
            }
        }
    }

    @Override
    public boolean filter(Object source, String propertyName) {

        log.debug("filter property name -> {}", propertyName);

        if (patterns.isEmpty()) {
            return isExcludeMode() ? true : false;
        }

        if (patterns.contains(propertyName)) {
            return isExcludeMode() ? false : true;
        }

        for (String pattern : patterns) {
            if (matcher.match(pattern, propertyName)) {
                return isExcludeMode() ? false : true;
            }
        }

        return isExcludeMode() ? true : false;
    }

    public boolean isExcludeMode() {
        return excludeMode;
    }

    public Set<String> getPatterns() {
        return patterns;
    }

}