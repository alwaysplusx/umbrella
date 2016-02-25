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
package com.harmony.umbrella.ws.ext;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.ext.LogUtils;
import com.harmony.umbrella.monitor.ext.LogUtils.GraphFormat;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ProxyExecutor;
import com.harmony.umbrella.ws.visitor.AbstractContextVisitor;

/**
 * webservice客户端同步部分的日志记录工具类
 * 
 * @author wuxii@foxmail.com
 */
public class HarmonyContextVisitor extends AbstractContextVisitor {

    private static final Logger log = LoggerFactory.getLogger(HarmonyContextVisitor.class);

    @Override
    public void visitFinally(Object result, Throwable throwable, final Context context) {
        MethodGraph graph = (MethodGraph) context.get(ProxyExecutor.WS_EXECUTION_GRAPH);

        if (graph != null) {

            LogUtils.log(graph, new GraphFormat<MethodGraph>() {

                @Override
                public String format(MethodGraph graph) {

                    StringBuilder buf = new StringBuilder();
                    try {

                        buf.append("接口名称:").append(StringUtils.getMethodId(context.getMethod())).append("\n");
                        buf.append("服务地址:").append(context.getAddress()).append("\n");

                        String username = context.getUsername();
                        if (username != null) {
                            buf.append("用户名称:").append(username).append("\n");
                        }

                        String password = context.getPassword();
                        if (password != null) {
                            buf.append("用户密码:").append(password).append("\n");
                        }

                        buf.append("详细参数:").append(LogUtils.parameterToJson(graph.getMethodArguments())).append("\n");
                        buf.append("返回结果:");
                        Method method = graph.getMethod();
                        if (method.getReturnType() == void.class) {
                            buf.append("(void)");
                        } else {
                            buf.append(Json.toJson(graph.getMethodResult()));
                        }
                        buf.append("\n");

                        buf.append("交互耗时:").append(graph.use()).append("ms\n");

                        if (graph.isException()) {
                            buf.append("异常信息:").append(Exceptions.getRootCause(graph.getException())).append("\n");
                        }

                    } catch (NoSuchMethodException e) {
                        log.warn("", e);
                    }

                    return buf.toString();
                }

            });

        }
    }

}