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
package com.harmony.umbrella.monitor.matcher;

import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.util.AntPathMatcher;

/**
 * AntPathMatcher adapter
 * 
 * @author wuxii@foxmail.com
 * @see AntPathMatcher
 */
public class UrlPathMatcher extends AntPathMatcher implements ResourceMatcher<String> {

    @Override
    public boolean match(String pattern, String path) {
        return super.match(pattern, path);
    }

}
