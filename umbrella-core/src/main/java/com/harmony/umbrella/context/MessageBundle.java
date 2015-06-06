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
package com.harmony.umbrella.context;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.harmony.umbrella.util.Assert;

/**
 * 国际化工具类
 * 
 * @author wuxii@foxmail.com
 */
public class MessageBundle {

    public static final String DEFAULT_BASE_NAME = "Message";

    /**
     * 系统默认的本地化
     */
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();

    /**
     * 国际化的资源映射
     */
    private static final Map<String, MessageBundle> bundles = new HashMap<String, MessageBundle>();

    /**
     * 国际化资源
     */
    private final ResourceBundle bundle;

    /**
     * 当前的本地化
     */
    private final Locale defaultLocale;

    /**
     * 资源的基础名称
     */
    private final String baseName;

    private MessageBundle(String baseName, Locale locale) {
        Assert.notNull(locale, "locale can't be null");
        Assert.notNull(baseName, "resource bundle can't be null");
        this.baseName = baseName;
        this.defaultLocale = getLocale(locale);
        this.bundle = ResourceBundle.getBundle(baseName, this.defaultLocale);
    }

    public static MessageBundle getInstance(Locale locale) {
        return getInstance(DEFAULT_BASE_NAME, locale);
    }

    /**
     * 创建默认为{@linkplain #DEFAULT_LOCALE}的国际化资源
     * 
     * @param baseName
     *            资源的基础名称
     * @return
     */
    public static MessageBundle getInstance(String baseName) {
        return getInstance(baseName, DEFAULT_LOCALE);
    }

    /**
     * 创建国际化资源
     * 
     * @param baseName
     *            资源基础名称
     * @param locale
     *            本地化
     * @return
     */
    public static MessageBundle getInstance(String baseName, Locale locale) {
        String key = getKey(baseName, locale == null ? DEFAULT_LOCALE : getLocale(locale));
        MessageBundle ms = bundles.get(key);
        if (ms == null) {
            synchronized (bundles) {
                if (!bundles.containsKey(key)) {
                    bundles.put(key, ms = new MessageBundle(baseName, locale));
                }
            }
        }
        return ms;
    }

    /**
     * @param code
     * @return
     */
    public String getMessage(String code) {
        return getMessage(code, defaultLocale);
    }

    public String getMessage(String code, Locale locale) {
        return getMessage(code, null, locale == null ? defaultLocale : locale);
    }

    public String getMessage(String code, Object[] args, Locale locale) {
        return getMessage(code, args, "", locale == null ? defaultLocale : locale);
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        locale = locale == null ? defaultLocale : locale;
        if (!isCurrent(locale)) {
            MessageBundle ms = getInstance(baseName, locale);
            return ms.getMessage(code, args, defaultMessage, locale);
        }
        String message = "";
        try {
            message = bundle.getString(code);
        } catch (MissingResourceException e) {
        }
        return MessageFormat.format(message, args);
    }

    private static String getKey(String baseName, Locale locale) {
        return baseName + "#" + (locale != null ? locale.toString() : "");
    }

    private boolean isCurrent(Locale locale) {
        return this.defaultLocale.equals(getLocale(locale));
    }

    public static Locale getLocale(Locale locale) {
        if (locale == null)
            return null;
        return getLocale(locale.toString());
    }

    public static Locale getLocale(String locale) {
        for (Locale loc : Locale.getAvailableLocales()) {
            if (loc.toString().equalsIgnoreCase(locale)) {
                return loc;
            }
        }
        return null;
    }

}
