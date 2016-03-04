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
package com.harmony.umbrella.log.jdbc;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * @author wuxii@foxmail.com
 */
@Plugin(name = "Field", category = "Core", printObject = true)
public class FieldColumnConfig {

    public FieldColumnConfig(Configuration config, String field, String column) {
    }

    @PluginFactory
    public static FieldColumnConfig createFieldColumnConfig(//
            @PluginConfiguration Configuration config, //
            @PluginAttribute("filed") final String field,//
            @PluginAttribute("column") final String column) {
        return new FieldColumnConfig(config, field, column);
    }

}
