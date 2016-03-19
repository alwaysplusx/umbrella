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
 * 监控视图监听
 * 
 * @author wuxii@foxmail.com
 */
public interface GraphListener<T extends Graph> {

    /**
     * 视图创建完成后被调用
     * 
     * @param graph
     *            完成的监控视图
     */
    void analyze(T graph);

}
