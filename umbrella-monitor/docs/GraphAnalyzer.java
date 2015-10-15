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
 * 监控结果视图分析处理工具，可用于处理分析结果信息，或将监控结果保存做二次处理等。
 * <p>
 * 监控的结果信息由{@linkplain Monitor}生成
 * 
 * @deprecated replace by {@linkplain GraphListener}
 * 
 * @param <T>
 *            被解析的监控视图类型
 * @author wuxii@foxmail.com
 */
@Deprecated
public interface GraphAnalyzer<T extends Graph> {

    /**
     * 分析结果
     * 
     * @param graph
     *            监控的结果
     */
    void analyze(T graph);

}
