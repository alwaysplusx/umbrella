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
package com.harmony.umbrella.web;

import com.harmony.umbrella.Constants;

/**
 * @author wuxii@foxmail.com
 */
public class WebConstants extends Constants {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public class MediaType {

        private static final String charset = "charset=utf-8";

        public final static String WILDCARD = "*/*; " + charset;

        public static final String TEXT_HTML = "text/html; " + charset;
        public static final String TEXT_JSON = "text/json; " + charset;
        public static final String TEXT_XML = "text/xml; " + charset;
        public static final String TEXT_PLAIN = "text/plain; " + charset;

        public static final String APPLICATION_HTML = "application/html; " + charset;
        public static final String APPLICATION_JSON = "application/json; " + charset;
        public static final String APPLICATION_XML = "application/xml; " + charset;
        public static final String APPLICATION_TEXT = "application/text; " + charset;

    }

}
