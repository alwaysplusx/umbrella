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
package com.harmony.umbrella.ws.ser;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 作为服务的返回消息
 * 
 * @author wuxii@foxmail.com
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -8204124664128300071L;

    /**
     * INFO
     */
    public static final String I = "I";
    /**
     * WARN
     */
    public static final String W = "W";
    /**
     * ERROR
     */
    public static final String E = "E";
    /**
     * SUCCESS
     */
    public static final String S = "S";

    /**
     * 服务的操作消息
     */
    protected String message;
    /**
     * 服务处理的结果类型
     */
    protected String type;
    /**
     * 服务处理的中间结果
     */
    protected final Map<String, String> content = new HashMap<String, String>();

    public Message() {
    }

    public Message(String message, String type) {
        this(message, type, Collections.<String, String> emptyMap());
    }

    public Message(String message, String type, Map<String, String> content) {
        this.message = message;
        this.type = type;
        if (content != null && !content.isEmpty()) {
            this.content.putAll(content);
        }
    }

    /**
     * 消息的类型
     * <ul>
     * <li>I - info
     * <li>W - warn
     * <li>E - error
     * <li>S - success
     * </ul>
     * 
     * @return 消息的标识
     */
    public String getType() {
        return type;
    }

    /**
     * 消息的类型
     * <ul>
     * <li>I - info
     * <li>W - warn
     * <li>E - error
     * <li>S - success
     * </ul>
     * 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 返回的消息
     * 
     * @return 文本消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置消息文本
     * 
     * @param message
     *            消息文本
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 必须, 大多数框架解析的为getter方法.
     */
    public Map<String, String> getContent() {
        return content;
    }

    /**
     * 设置服务的内容
     * 
     * @param content
     *            服务内容
     */
    public void setContent(Map<String, String> content) {
        this.content.clear();
        this.content.putAll(content);
    }

    /**
     * 增加消息节点
     * 
     * @param key
     *            消息节点的key
     * @param value
     *            消息节点的value
     */
    public void addContent(String key, String value) {
        content.put(key, value);
    }

    /**
     * 在原有基础上添加所有内容
     * 
     * @param content
     */
    public void addAllContent(Map<String, String> content) {
        if (content != null && !content.isEmpty()) {
            this.content.putAll(content);
        }
    }

    @Override
    public String toString() {
        return "{type:" + type + ", message:" + message + ", content:" + content + "}";
    }

}