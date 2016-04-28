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
package com.harmony.umbrella.ws.service;

import static com.harmony.umbrella.ws.service.Message.*;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.groups.Default;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.harmony.umbrella.i18n.MessageBundle;
import com.harmony.umbrella.i18n.ResourceMessageBundle;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.validator.ValidVisitor;
import com.harmony.umbrella.validator.Validators;
import com.harmony.umbrella.ws.util.BeanMapper;

/**
 * 同步服务端部分
 *
 * @author wuxii@foxmail.com
 */
public abstract class ServiceSupport {

    @Resource
    protected WebServiceContext webServiceContext;

    protected static final Log LOG = Logs.getLog(ServiceSupport.class);

    protected static final String MAPPING_LOCATION = "mapping.xml";

    // 支持国际化的key - 默认国际化资源文件为WsMessage
    public static final String MESSAGE_ERROR = "{harmony.server.error}";
    public static final String MESSAGE_SUCCESS = "{harmony.server.success}";
    public static final String MESSAGE_FAILED = "{harmony.server.failed}";

    private MessageBundle messageBundle;

    private Map<Class<?>, KeyAccessMember[]> keyAccessMemberCache = new HashMap<Class<?>, KeyAccessMember[]>();

    protected Locale locale;

    protected boolean extractContent = true;

    protected String getMappingLocation() {
        return MAPPING_LOCATION;
    }

    /**
     * 默认加载类路径下的配置文件mapping.xml
     *
     * @return 对象映射工具
     */
    protected BeanMapper getMapper() {
        BeanMapper mapper = null;
        String mappingFile = getMappingLocation() == null ? MAPPING_LOCATION : getMappingLocation();
        if (exists(mappingFile)) {
            mapper = BeanMapper.getInstance(mappingFile);
            LOG.debug("use mapper with file {}", mappingFile);
        } else {
            mapper = BeanMapper.getInstance();
            LOG.debug("use default mapper");
        }
        return mapper;
    }

    private boolean exists(String mappingFile) {
        URL result = ClassUtils.getDefaultClassLoader().getResource(mappingFile);
        if (result == null && ClassUtils.getDefaultClassLoader() != ServiceSupport.class.getClassLoader()) {
            ClassLoader classLoader = ServiceSupport.class.getClassLoader();
            if (classLoader != null) {
                result = classLoader.getResource(mappingFile);
            }
        }
        if (result == null) {
            result = ClassLoader.getSystemResource(mappingFile);
        }
        return result != null;
    }

    /**
     * 对象映射
     *
     * @param src
     *            源对象
     * @param destType
     *            目标对象类型
     * @param <S>
     *            源类型
     * @param <D>
     *            目标类型
     * @return dest object
     */
    protected <S, D> D mapping(S src, Class<D> destType) {
        return getMapper().mapper(src, destType);
    }

    /**
     * 对象映射
     *
     * @param src
     *            源对象
     * @param dest
     *            目标对象
     * @param <S>
     *            源类型
     * @param <D>
     *            目标类型
     * @return dest object
     */
    protected <S, D> D mapping(S src, D dest) {
        return getMapper().mapper(src, dest);
    }

    /**
     * 对象映射
     *
     * @param src
     *            源
     * @param dest
     *            目标
     * @param mapId
     *            配置文件中的mapId
     * @param <S>
     *            源类型
     * @param <D>
     *            目标类型
     * @return dest object
     */
    protected <S, D> D mapping(S src, D dest, String mapId) {
        return getMapper().mapper(src, dest, mapId);
    }

    /**
     * 对象映射
     *
     * @param src
     *            源
     * @param destType
     *            目标
     * @param <S>
     *            源类型
     * @param <D>
     *            目标类型
     * @param mapId
     *            配置文件中的mapId
     * @return
     */
    protected <S, D> D mapping(S src, Class<D> destType, String mapId) {
        return getMapper().mapper(src, destType, mapId);
    }

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
        return error(MESSAGE_ERROR, new HashMap<String, String>());
    }

    /**
     * @see #error()
     */
    protected Message error(Exception ex) {
        return error(MESSAGE_ERROR, ex);
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
        return error(MESSAGE_ERROR, content);
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

    /**
     * 在执行上下文中根据key增加描述字符, 与原字符用','隔开 如果原上下文中没有对应的key，则直接添加到上下文中
     *
     * @param key
     *            上下文中的key
     * @param value
     *            描述字符
     * @param content
     *            执行的上下文
     */
    private void append(String key, String value, Map<String, String> content) {
        String msg = content.get(key);
        if (StringUtils.isBlank(msg)) {
            content.put(key, value);
        } else {
            content.put(key, String.format("%s, %s", msg, value));
        }
    }

    /**
     * 创建服务端返回消息
     */
    private Message buildMessage(String message, String type, Map<String, String> content) {
        message = message.trim();
        if (message.startsWith("{") && message.endsWith("}")) {
            message = getText(message.substring(1, message.length() - 1));
        }
        if (!extractContent) {
            return new Message(message, type, content);
        }
        return new Message(extractContentMessage(new StringBuilder(message), content), type);
    }

    /**
     * 根据key获取国际化文本
     *
     * @param name
     *            国际化文本key
     * @return 国际化对应值
     */
    protected String getText(String name) {
        return getMessageBundle().getString(name);
    }

    protected MessageBundle getMessageBundle() {
        if (messageBundle == null) {
            messageBundle = new ResourceMessageBundle("WsMessage", getLocale());
        }
        return messageBundle;
    }

    protected Locale getLocale() {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
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

    // 服务端接收的值处理, 获取对应的vo唯一key

    /**
     * 根据{@linkplain com.harmony.umbrella.ws.annotation.Key Key}获取传入的vo的对于key值
     *
     * @param obj
     *            传入的vo
     * @return vo key
     */
    protected String getKey(Object obj) {
        StringBuilder sb = new StringBuilder();
        KeyAccessMember[] kams = getKeyAccessMember(obj.getClass());
        for (int i = 0, max = kams.length;; i++) {
            // FIXME key access member
            sb.append(kams[i].getMemberName()).append(":").append(kams[i].get(obj));
            if (i < max) {
                sb.append(", ");
            } else {
                break;
            }
        }
        return sb.toString();
    }

    protected KeyAccessMember[] getKeyAccessMember(Class<?> targetClass) {
        KeyAccessMember[] members = keyAccessMemberCache.get(targetClass);
        if (members == null) {
            members = ServiceUtils.getKeyAccessMember(targetClass);
            ServiceUtils.sortKeyAccessMember(members);
            keyAccessMemberCache.put(targetClass, members);
        }
        return members;
    }

    // 服务输入vo的检验方法

    /**
     * @see #isValid(Collection, MessageContent, ValidVisitor, Class...)
     */
    protected boolean isValid(Collection<?> objs, MessageContent content) {
        return isValid(objs, content, (ValidVisitor) null);
    }

    /**
     * @see #isValid(Object, MessageContent, ValidVisitor, Class...)
     */
    protected boolean isValid(Collection<?> objs, MessageContent content, Class<?>... groups) {
        return isValid(objs, content, null, groups);
    }

    /**
     * 验证vo集合
     *
     * @param objs
     *            vo集合
     * @param content
     *            服务端上下文
     * @param visitor
     *            自定义验证工具
     * @param groups
     *            验证组
     * @return
     * @see #isValid(Object, MessageContent, ValidVisitor, Class...)
     */
    protected boolean isValid(Collection<?> objs, MessageContent content, ValidVisitor visitor, Class<?>... groups) {
        boolean flag = true;
        for (Object object : objs) {
            flag = flag && isValid(object, content, visitor, groups);
        }
        return flag;
    }

    /**
     * @see #isValid(Object, MessageContent, ValidVisitor, Class...)
     */
    protected boolean isValid(Object obj, MessageContent content) {
        return isValid(obj, content, Default.class);
    }

    /**
     * @see #isValid(Object, MessageContent, ValidVisitor, Class...)
     */
    protected boolean isValid(Object obj, MessageContent content, Class<?>... groups) {
        return isValid(obj, content, null, groups);
    }

    /**
     * 验证vo是否符合数据格式的要求。一般地，在开始业务时候会把验证放置在前提条件
     * <p/>
     * 如果验证不通过则将在content中添加对于的vo key以及vo验证错误的信息
     *
     * @param obj
     *            传入的vo
     * @param content
     *            服务的上下文
     * @param visitor
     *            自定义的验证工具允许为空
     * @param groups
     *            验证的group
     * @return true验证通过， false验证vo存在错误
     */
    protected boolean isValid(Object obj, MessageContent content, ValidVisitor visitor, Class<?>... groups) {
        Assert.notNull(content, "message content must not be null");
        if (obj == null) {
            content.append("NULL", "input is null");
            return false;
        }
        String message = Validators.getViolationMessage(obj, visitor, groups);
        if (StringUtils.isNotBlank(message)) {
            String key = getKey(obj);
            content.append(key, message);
            return false;
        }
        return true;
    }

    /**
     * 创建返回消息的内容
     *
     * @return new {@linkplain MessageContent}
     */
    protected MessageContent createContent() {
        return new MessageContent();
    }

    protected final HttpServletRequest getHttpRequest() {
        return (HttpServletRequest) this.webServiceContext.getMessageContext().get(MessageContext.SERVLET_REQUEST);
    }

    protected final HttpServletResponse getHttpResponse() {
        return (HttpServletResponse) this.webServiceContext.getMessageContext().get(MessageContext.SERVLET_RESPONSE);
    }

}
