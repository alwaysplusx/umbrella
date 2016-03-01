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
package com.harmony.umbrella.log;

import java.text.ParseException;

/**
 * @author wuxii@foxmail.com
 */
public interface LogFormat<T> {

    String JSON_TYPE = "json";

    String HTML_TYPE = "html";

    String XML_TYPE = "xml";

    T format(LogInfo logInfo);

    LogInfo parse(T message) throws ParseException;

    boolean canParse(T message);

    String formatType();

}
