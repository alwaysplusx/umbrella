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
package com.harmony.umbrella.monitor.ext;

import static com.harmony.umbrella.Constants.*;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.commons.log.Log4jUtils;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.monitor.Graph;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class LogUtils {

    private static final Logger log = LoggerFactory.getLogger(LogUtils.class);

    public static final boolean log(MethodGraph graph, GraphFormat<MethodGraph> graphFormat) {
        try {

            String module = LogUtils.getLogModule(graph);

            String from = LogUtils.getLogFromName(graph);

            String category = LogUtils.getLogCategory(graph);

            String result = graph.isException() ? "异常" : "正常";

            String logMessage = LogUtils.format(module, result, from, category, graphFormat.format(graph));

            if (graph.isException()) {
                Log4jUtils.logSysError(logMessage, graph.getException());
            } else {
                Log4jUtils.logSysInfo(logMessage, null);
            }

        } catch (Exception e) {
            log.warn("", e);
            return false;
        }
        return true;
    }

    public static final boolean log(MethodGraph graph) {
        return log(graph, new SimpleMethodGraphFormat());
    }

    /**
     * 由视图找寻模块名称
     * 
     * @param graph
     *            视图结果
     * @return 模块名称
     * @see #getLogModule(Class)
     */
    public static String getLogCategory(MethodGraph graph) {
        String category = graph.getCategory();
        if (StringUtils.isBlank(category) && graph.getTargetClass() != null) {
            category = getLogCategory(graph.getTargetClass().getName());
        }
        return category == null ? "" : category;
    }

    /**
     * 获取日志大类, 未找到返回空字符
     * 
     * @param key
     *            key
     * @return 日志大类
     */
    public static String getLogCategory(String key) {
        return getProperty(key, "");
    }

    /**
     * 由视图找寻模块名称
     * 
     * @param graph
     *            视图结果
     * @return 模块名称
     * @see #getLogModule(Class)
     */
    public static String getLogModule(MethodGraph graph) {
        String module = graph.getModule();
        if (StringUtils.isBlank(module)) {
            module = getLogModule(graph.getTargetClass());
        }
        return module;
    }

    /**
     * 由视图找寻日志来源名称
     * 
     * @param graph
     *            视图结果
     * @return 日志来源
     */
    public static String getLogFromName(MethodGraph graph) {
        String from = graph.getOperator();
        if (StringUtils.isBlank(from)) {
            String methodName = graph.getMethod() == null ? "" : graph.getMethod().getName();
            from = getLogFromName(graph.getTargetClass(), methodName);
        }
        return from;
    }

    /**
     * 获取模块名称, 如果未找到返回类名
     * 
     * @param clazz
     *            类名
     * @return 未找到返回类名
     */
    public static final String getLogModule(Class<?> clazz) {
        return getProperty(clazz.getName(), clazz.getName());
    }

    /**
     * 根据服务类名以及方法名获取服务对应的中文名称
     * 
     * @param proxyClass
     *            服务类
     * @param methodName
     *            服务的方法
     * @return 服务的中文名称, 如果没有找到返回 proxyClass + methodName
     */
    public static final String getLogFromName(Class<?> clazz, String methodName) {
        String key = clazz.getName() + "." + methodName;
        if (containsKey(key)) {
            return getProperty(key);
        }
        return getProperty(clazz.getName(), key);
    }

    /**
     * 根据名称获取服务的中文名称
     * 
     * @param key
     *            服务key
     * @return 如果未找到返回key
     */
    public static final String getLogFromName(String key) {
        return getProperty(key, key);
    }

    /**
     * @param model
     *            系统模块
     * @param message
     *            操作结果
     * @param from
     *            日志来源
     * @param category
     *            分类标识
     * @param result
     *            日志信息
     * @return 格式化后的日志消息
     */
    public static final String format(String model, String result, String from, String category, String message) {
        StringBuilder sb = new StringBuilder();

        sb.append(model).append("|");//
        sb.append(result).append("|");//
        sb.append(from).append("|");//
        sb.append(category).append("|");//
        sb.append(message);

        return sb.toString();
    }

    /**
     * 将参数转为json格式，参数名称为数组中的索引
     * 
     * @param parameters
     *            参数
     * @return json格式的参数
     */
    public static String parameterToJson(Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return "{}";
        }
        Map<Integer, Object> param = new LinkedHashMap<Integer, Object>(parameters.length);

        for (int i = 0, max = parameters.length; i < max; i++) {
            param.put(i, parameters[i]);
        }

        return Json.toJson(param);
    }

    public interface GraphFormat<T extends Graph> {
        String format(T graph);

    }

    public static final class SimpleMethodGraphFormat implements GraphFormat<MethodGraph> {

        @Override
        public String format(MethodGraph graph) {

            StringBuilder buf = new StringBuilder();

            buf.append("服务名称:").append(StringUtils.getMethodId(graph.getMethod())).append("\n");

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

            if (log.isDebugEnabled()) {
                buf.append("请求信息:").append(graph.getJsonArguments()).append("\n");
                buf.append("返回信息:").append(graph.getJsonResult()).append("\n");
            }

            return buf.toString();
        }

    }

}
