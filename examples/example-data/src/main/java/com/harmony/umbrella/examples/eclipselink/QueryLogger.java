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
package com.harmony.umbrella.examples.eclipselink;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class QueryLogger extends AbstractSessionLog {

    private static final Log log = Logs.getLog(QueryLogger.class);

    @Override
    public void log(SessionLogEntry entry) {
        if ("sql".equals(entry.getNameSpace()) && StringUtils.isNotBlank(entry.getMessage())) {
            String message = entry.getMessage().toLowerCase();
            if (message.startsWith("select")) {
                log.info(message);
            } else if (message.startsWith("update")) {

            } else if (message.startsWith("delete")) {

            } else if (message.startsWith("insert")) {

            }
        }
    }
}
