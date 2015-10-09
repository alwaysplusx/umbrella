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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.commons.log.Log4jUtils;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.PropUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ProxyExecutor;
import com.harmony.umbrella.ws.visitor.AbstractContextVisitor;

/**
 * @author wuxii@foxmail.com
 */
public class HarmonyContextVisitor extends AbstractContextVisitor {

    public static final String FROM_PROPERTIES_FILE_LOCATION = "META-INF/jaxws/proxyName.properties";

    private static final Logger log = LoggerFactory.getLogger(HarmonyContextVisitor.class);

    /**
     * 各个接口对应的名称, 接口类 + '.' + 方法名称
     */
    private final Properties fromProps = new Properties();

    public HarmonyContextVisitor() {
        fromProps.putAll(PropUtils.loadProperties(FROM_PROPERTIES_FILE_LOCATION));
    }

    @Override
    public void visitFinally(Object result, Throwable throwable, Context context) {
        MethodGraph graph = (MethodGraph) context.get(ProxyExecutor.WS_EXECUTION_GRAPH);

        if (graph != null) {

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

                buf.append("交互耗时:").append(graph.use()).append("ms\n");
                buf.append("返回结果:").append(Json.toJson(graph.getMethodResult())).append("\n");

                if (graph.isException()) {
                    buf.append("异常信息:").append(Exceptions.getRootCause(graph.getException())).append("\n");
                }

                buf.append("详细参数:").append(LogUtils.parameterToJson(context.getParameters()));

                log(buf.toString(), getServiceFrom(context), throwable);

            } catch (NoSuchMethodException e) {
                log.error("invalid context {}", context, e);
            }

        } else {
            log.warn("not context graph for context {}", context);
        }

    }

    private String getServiceFrom(Context context) {
        Class<?> serviceInterface = context.getServiceInterface();
        String key = serviceInterface.getName() + "." + context.getMethodName();
        if (fromProps.containsKey(key)) {
            return fromProps.getProperty(key);
        }
        return fromProps.getProperty(serviceInterface.getName(), "");
    }

    private void log(String message, String from, Throwable e) {

        String result = e == null ? "无系统异常" : "异常";
        String logMessage = LogUtils.format("接口同步", result, from, "WP-100000", message);

        if (e != null) {
            Log4jUtils.logSysError(logMessage, e);
        } else {
            Log4jUtils.logSysInfo(logMessage, null);
        }

    }
}
