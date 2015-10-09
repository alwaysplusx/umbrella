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
package com.harmony.umbrella.ws.ext;

import java.util.LinkedHashMap;
import java.util.Map;

import com.harmony.umbrella.json.Json;

/**
 * @author wuxii@foxmail.com
 */
public class LogUtils {

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
            return "";
        }
        Map<Integer, Object> param = new LinkedHashMap<Integer, Object>(parameters.length);

        for (int i = 0, max = parameters.length; i < max; i++) {
            param.put(i, parameters[i]);
        }

        return Json.toJson(param);
    }

}
