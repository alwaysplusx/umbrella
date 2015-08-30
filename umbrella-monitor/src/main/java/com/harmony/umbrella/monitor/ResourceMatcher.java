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
package com.harmony.umbrella.monitor;

/**
 * 资源匹配器
 * <p>
 * 如:监控http资源，使用url匹配原则。 方法则匹配方法签名
 * 
 * @author wuxii@foxmail.com
 * @see com.harmony.umbrella.monitor.matcher.UrlPathMatcher
 * @see com.harmony.umbrella.monitor.matcher.MethodExpressionMatcher
 */
public interface ResourceMatcher<T> {

    /**
     * 匹配资源是否符合当前定义的规则
     * 
     * @param pattern
     *            资源的模版
     * @param resource
     *            待检查的资源
     * @return true匹配成功
     */
    boolean match(String pattern, T resource);

}
