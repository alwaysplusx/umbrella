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
package com.harmony.umbrella.ws.jaxws;

import com.harmony.umbrella.monitor.MethodMonitor.MethodGraph;

/**
 * 监视扩展，将jaxws的运行情况保存到数据库或者其他方式存储起来
 * 
 * @author wuxii@foxmail.com
 */
public interface JaxWsGraph extends MethodGraph {

    String JAXWS_CONTEXT_GRAPH = JaxWsGraph.class.getName() + ".graph";

}
