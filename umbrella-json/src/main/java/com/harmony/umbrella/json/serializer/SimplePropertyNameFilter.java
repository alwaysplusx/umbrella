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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.io.util.AntPathMatcher;
import com.harmony.umbrella.io.util.PathMatcher;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class SimplePropertyNameFilter extends PropertyNameFilter {

    private static final Logger log = LoggerFactory.getLogger(SimplePropertyNameFilter.class);

    private PathMatcher matcher;
    /**
     * 过滤策略
     */
    private FilterPolicy policy;
    /**
     * 过滤的模版
     */
    private final Set<String> patterns = new HashSet<String>();

    public SimplePropertyNameFilter(Set<String> patterns) {
        this(FilterPolicy.EXCLUDE, new AntPathMatcher(), patterns.toArray(new String[patterns.size()]));
    }

    public SimplePropertyNameFilter(String... patterns) {
        this(FilterPolicy.EXCLUDE, new AntPathMatcher(), patterns);
    }

    public SimplePropertyNameFilter(FilterPolicy policy, String... patterns) {
        this(policy, new AntPathMatcher(), patterns);
    }

    public SimplePropertyNameFilter(PathMatcher matcher, String... patterns) {
        this(FilterPolicy.EXCLUDE, matcher, patterns);
    }

    public SimplePropertyNameFilter(FilterPolicy policy, PathMatcher matcher, String... patterns) {
        Assert.notNull(matcher, "matcher must not be null");
        Assert.notNull(policy, "filter policy must not be null");
        this.matcher = matcher;
        this.policy = policy;
        for (String property : patterns) {
            if (StringUtils.isNotBlank(property)) {
                this.patterns.add(property);
                if (property.startsWith("*.")) {
                    this.patterns.add(property.substring(2));
                }
            }
        }
    }

    @Override
    public boolean filter(Object source, String propertyName) {

        log.debug("filter property name -> {}", propertyName);

        if (patterns.isEmpty()) {
            return isExclude() ? true : false;
        }

        if (patterns.contains(propertyName)) {
            return isExclude() ? false : true;
        }

        for (String pattern : patterns) {
            if (matcher.match(pattern, propertyName)) {
                return isExclude() ? false : true;
            }
        }

        return isExclude() ? true : false;
    }

    public boolean isExclude() {
        return policy == FilterPolicy.EXCLUDE;
    }

    public boolean isInclude() {
        return policy == FilterPolicy.INCLUDE;
    }

    public FilterPolicy getFilterPolicy() {
        return policy;
    }

    public Set<String> getExcludes() {
        return patterns;
    }

    public enum FilterPolicy {
        EXCLUDE, INCLUDE
    }

}