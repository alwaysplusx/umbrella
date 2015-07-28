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
package com.harmony.umbrella.ws;

import com.harmony.umbrella.monitor.MethodMonitor.MethodGraph;

/**
 * 监控扩展，将WebService的运行情况保存到数据库或者其他方式存储起来
 * 
 * @author wuxii@foxmail.com
 */
public interface WebServiceGraph extends MethodGraph {

    /**
     * 可作为{@link Context#get(String)}对应的Key
     */
    String WS_CONTEXT_GRAPH = WebServiceGraph.class.getName() + ".graph";

}
