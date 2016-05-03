package com.harmony.umbrella.ws.service;

import java.util.HashMap;

import com.harmony.umbrella.util.StringUtils;

/**
 * 作为服务的返回的服务消息的内容
 * 
 * @author wuxii@foxmail.com
 */
public class MessageContent extends HashMap<String, String> {

    private static final long serialVersionUID = 8111278858572162151L;

    /**
     * 在原有的key基础上添加字符, 如果原来不存在对应的key则直接添加
     * 
     * @param key
     *            key值
     * @param value
     *            对应的值
     */
    public void append(String key, String value) {
        String msg = get(key);
        if (StringUtils.isBlank(msg)) {
            put(key, value);
        } else {
            put(key, String.format("%s, %s", msg, value));
        }
    }
}
