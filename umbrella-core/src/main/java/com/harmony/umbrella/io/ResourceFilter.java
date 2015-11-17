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
package com.harmony.umbrella.io;

/**
 * 资源过滤器
 * 
 * @author wuxii@foxmail.com
 */
public interface ResourceFilter {

    /**
     * 过滤资源，通过过滤条件返回true, 不通过返回false
     * 
     * @param resource
     *            待过滤的资源
     * @return 通过true, 不通过false
     */
    boolean accept(Resource resource);

}