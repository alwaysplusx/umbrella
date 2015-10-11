/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.logger.ext;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.appender.db.jdbc.JdbcDatabaseManager;

/**
 * @author wuxii@foxmail.com
 */
public class HarmonyJdbcAppender extends AbstractDatabaseAppender<JdbcDatabaseManager> {

    private static final long serialVersionUID = 1976362455170301408L;

    protected HarmonyJdbcAppender(String name, Filter filter, boolean ignoreExceptions, JdbcDatabaseManager manager) {
        super(name, filter, ignoreExceptions, manager);
    }

}
