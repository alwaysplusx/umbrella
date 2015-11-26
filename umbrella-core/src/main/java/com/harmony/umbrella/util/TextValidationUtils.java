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
package com.harmony.umbrella.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuxii@foxmail.com
 */
public class TextValidationUtils {

    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final String REGEX_PHONENUMBER = "^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";

    public static boolean isEmail(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        Pattern regex = Pattern.compile(REGEX_EMAIL);
        Matcher matcher = regex.matcher(text);
        return matcher.matches();
    }

    public static boolean isPhoneNumber(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        Pattern regex = Pattern.compile(REGEX_PHONENUMBER);
        Matcher matcher = regex.matcher(text);
        return matcher.matches();
    }

}
