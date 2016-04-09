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
package com.harmony.umbrella.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceMessageBundle implements MessageBundle {

    private ResourceBundle resourceBundle;
    private Locale locale;

    public ResourceMessageBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.locale = resourceBundle.getLocale();
    }

    public ResourceMessageBundle(String baseName, Locale locale) {
        this.resourceBundle = ResourceBundle.getBundle(baseName, locale);
        this.locale = locale;
    }

    @Override
    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    public String getString(String key, Object... objects) {
        String text = resourceBundle.getString(key);
        return String.format(text, objects);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

}
