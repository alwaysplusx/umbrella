/*
 * Copyright 2012-2016 the original author or authors.
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
package com.harmony.umbrella.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author wuxii@foxmail.com
 */
public class I18NUtils {

    public static final String DEFAULT_BASE_NAME = "Message";

    /**
     * 系统默认的本地化
     */
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();

    public static MessageBundle getDefaultMessageBundle(String baseName) {
        return getMessageBundle(baseName, getDefaultLocale());
    }

    public static MessageBundle getMessageBundle(String baseName, Locale locale) {
        return new MessageBundle(baseName, locale);
    }

    public static Locale getSpecificationLocale(Locale locale) {
        if (locale == null) {
            return null;
        }
        Locale realLocale = getLocale(locale.toString());
        return realLocale == null ? locale : realLocale;
    }

    public static Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    public static Locale getLocale(Locale locale) {
        if (locale == null) {
            return null;
        }
        Locale realLocale = getLocale(locale.toString());
        return realLocale == null ? locale : realLocale;
    }

    public static Locale getLocale(String locale) {
        for (Locale loc : Locale.getAvailableLocales()) {
            if (loc.toString().equalsIgnoreCase(locale)) {
                return loc;
            }
        }
        return null;
    }

    public static class MessageBundle {

        /**
         * 国际化资源
         */
        private final ResourceBundle bundle;

        /**
         * 资源的基础名称
         */
        private final String baseName;

        private MessageBundle(String baseName, Locale locale) {
            this.baseName = baseName;
            this.bundle = ResourceBundle.getBundle(baseName, I18NUtils.getLocale(locale));
        }

        public String getString(String key) {
            return bundle.getString(key);
        }

        public String getBaseName() {
            return baseName;
        }

        public Locale getLocale() {
            return bundle.getLocale();
        }

        public ResourceBundle getResourceBundle() {
            return bundle;
        }

        public String getMessage(String key, Object... args) {
            String message = bundle.getString(key);
            return MessageFormat.format(message, args);
        }

    }
}
