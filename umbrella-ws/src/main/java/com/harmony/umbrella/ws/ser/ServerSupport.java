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

import static com.harmony.umbrella.ws.ser.Message.*;
import static com.harmony.umbrella.ws.ser.ServerValidation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.context.MessageBundle;
import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.validator.ValidVisitor;
import com.harmony.umbrella.validator.util.ValidatorUtils;
import com.harmony.umbrella.ws.ser.ServerValidation.MemberInvoker;

/**
 * 同步服务端部分
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ServerSupport {

    protected static final Logger LOG = LoggerFactory.getLogger(ServerSupport.class);

    public static final String MESSAG_ERROR = "{harmony.server.error}";
    public static final String MESSAGE_SUCCESS = "{harmony.server.success}";
    public static final String MESSAGE_FAILED = "{harmony.server.failed}";

    private final Map<Class<?>, List<MemberInvoker>> keyMembers = new HashMap<Class<?>, List<MemberInvoker>>();

    protected MessageBundle messageBundle = MessageBundle.getInstance("WsMessage", getLocale());

    protected boolean extract = true;

    /**
     * 便捷方法, 表示服务处理成功
     */
    protected Message success() {
        return success(MESSAGE_SUCCESS, new HashMap<String, String>());
    }

    /**
     * @see #success()
     */
    protected Message success(String message) {
        return success(message, new HashMap<String, String>());
    }

    /**
     * @see #success()
     */
    protected Message success(Map<String, String> content) {
        return success(MESSAGE_SUCCESS, content);
    }

    /**
     * @see #success()
     */
    protected Message success(String message, Map<String, String> content) {
        return buildMessage(message, S, content);
    }

    /**
     * 便捷方法，表示服务处理异常
     */
    protected Message error() {
        return error(MESSAG_ERROR, new HashMap<String, String>());
    }

    /**
     * @see #error()
     */
    protected Message error(Exception ex) {
        return error(MESSAG_ERROR, ex);
    }

    /**
     * @see #error()
     */
    protected Message error(String message, Exception ex) {
        return error(message, ex, new HashMap<String, String>());
    }

    /**
     * @see #error()
     */
    protected Message error(String message) {
        return error(message, new HashMap<String, String>());
    }

    /**
     * @see #error()
     */
    protected Message error(Map<String, String> content) {
        return error(MESSAG_ERROR, content);
    }

    /**
     * @see #error()
     */
    protected Message error(String message, Exception ex, Map<String, String> content) {
        append(ex.getClass().getName(), Exceptions.getAllMessage(ex), content);
        return error(message, content);
    }

    /**
     * @see #error()
     */
    protected Message error(String message, Map<String, String> content) {
        return buildMessage(message, E, content);
    }

    /**
     * 便捷方法，表示服务系统异常
     */
    protected Message failed() {
        return failed(MESSAGE_FAILED);
    }

    /**
     * @see #failed()
     */
    protected Message failed(String message) {
        return failed(message, new HashMap<String, String>());
    }

    /**
     * @see #failed()
     */
    protected Message failed(Map<String, String> content) {
        return failed(MESSAGE_FAILED, content);
    }

    /**
     * @see #failed()
     */
    protected Message failed(Exception ex) {
        return failed(MESSAGE_FAILED, ex, new HashMap<String, String>());
    }

    /**
     * @see #failed()
     */
    protected Message failed(Exception ex, Map<String, String> content) {
        return failed(MESSAGE_FAILED, ex, content);
    }

    /**
     * @see #failed()
     */
    protected Message failed(String message, Exception ex, Map<String, String> content) {
        append(ex.getClass().getName(), Exceptions.getAllMessage(ex), content);
        return failed(message, content);
    }

    /**
     * @see #failed()
     */
    protected Message failed(String message, Map<String, String> content) {
        return buildMessage(message, E, content);
    }

    private void append(String key, String value, Map<String, String> content) {
        String msg = content.get(key);
        if (StringUtils.isBlank(msg)) {
            content.put(key, value);
        } else {
            content.put(key, String.format("%s, %s", msg, value));
        }
    }

    private Message buildMessage(String message, String type, Map<String, String> content) {
        message = message.trim();
        if (message.startsWith("{") && message.endsWith("}")) {
            String code = message.substring(1, message.length() - 1);
            message = messageBundle.getMessage(code, getLocale());
        }
        if (!extract) {
            return new Message(message, type, content);
        }
        return new Message(extractContentMessage(new StringBuilder(message), content), type);
    }

    private Locale getLocale() {
        Locale locale = null;
        ApplicationContext context = ApplicationContext.getApplicationContext();
        CurrentContext cc = context.getCurrentContext();
        if (cc != null) {
            locale = cc.getLocale();
        } else {
            locale = context.getLocale();
        }
        return locale == null ? Locale.getDefault() : locale;
    }

    /**
     * 将在content中的内容全都驱赶到返回的消息message上
     * 
     * @param buf
     *            message
     * @param content
     *            返回content
     * @return message + content
     */
    protected String extractContentMessage(StringBuilder buf, Map<String, String> content) {
        if (buf == null) {
            buf = new StringBuilder();
        }
        if (content == null || content.isEmpty()) {
            return buf.toString();
        }
        if (buf.lastIndexOf("\n") != buf.length() && buf.lastIndexOf("/") != buf.length()) {
            buf.append("/");
        }
        Iterator<Entry<String, String>> it = content.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            buf.append("{").append(entry.getKey()).append(":").append(entry.getValue()).append("}");
            if (it.hasNext()) {
                buf.append("/");
            }
        }
        return buf.toString();
    }

    protected String getKey(Object obj) {
        return extractKey(obj);
    }

    private String extractKey(Object obj) {
        Map<String, Object> keys = new LinkedHashMap<String, Object>();
        MemberInvoker[] keyMembers = getKeyMembers(obj.getClass());
        for (MemberInvoker mi : keyMembers) {
            try {
                keys.put(mi.keyName(), mi.invoker(obj));
            } catch (InvokeException e) {
                LOG.warn("invok key member " + mi + " failed");
            }
        }
        return Json.toJson(keys, SerializerFeature.WriteMapNullValue);
    }

    protected boolean isValid(Iterator<?> objs, MessageContent content) {
        return isValid(objs, content, (ValidVisitor) null);
    }

    protected boolean isValid(Iterator<?> objs, MessageContent content, Class<?>... groups) {
        return isValid(objs, content, null, groups);
    }

    protected boolean isValid(Iterator<?> objs, MessageContent content, ValidVisitor visitor, Class<?>... groups) {
        boolean flag = true;
        while (objs.hasNext()) {
            flag = flag && isValid(objs.next(), content, visitor, groups);
        }
        return flag;
    }

    protected boolean isValid(Object obj, MessageContent content) {
        return isValid(obj, content, Default.class);
    }

    protected boolean isValid(Object obj, MessageContent content, Class<?>... groups) {
        return isValid(obj, content, null, groups);
    }

    protected boolean isValid(Object obj, MessageContent content, ValidVisitor visitor, Class<?>... groups) {
        long start = System.nanoTime();
        Assert.notNull(content, "message content must not be null");
        if (obj == null) {
            content.append("NULL", "input is null");
            return false;
        }
        String key = extractKey(obj);
        String message = ValidatorUtils.getViolationMessage(obj, visitor, groups);
        if (StringUtils.isNotBlank(message)) {
            content.append(key, message);
        }
        long use = System.nanoTime() - start;
        if (use > 500000000) {
            LOG.warn("valid obj[{}] is to complex, valid it use {}ns", obj, use);
        } else {
            LOG.debug("valid obj[{}], use {}ns", obj, use);
        }
        return !content.containsKey(key);
    }

    private MemberInvoker[] getKeyMembers(Class<?> clazz) {
        List<MemberInvoker> result = keyMembers.get(clazz);
        if (result == null) {
            result = Arrays.asList(getKeyMemberSortByKey(clazz));
            keyMembers.put(clazz, result);
        }
        return result.toArray(new MemberInvoker[result.size()]);
    }

    /**
     * 创建返回消息的内容
     * 
     * @return new {@linkplain MessageContent}
     */
    protected MessageContent createContent() {
        return new MessageContent();
    }

    protected void setExtract(boolean extract) {
        this.extract = extract;
    }

}
