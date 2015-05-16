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
package com.harmony.umbrella;

/**
 * @author wuxii@foxmail.com
 */
public abstract class Constant {

    private static final String defaultPackage = "com.harmony";

    public static final String GLOBAL_CONFIG = "META-INF/application.properties";

    public static final String DEFAULT_PACKAGE = defaultPackage;

    // public static final Properties GLOBAL_PROPERTIES;
    //
    // static {
    // Properties props = new Properties();
    // try {
    // props.putAll(PropUtils.loadProperties(GLOBAL_CONFIG));
    // } catch (IOException e) {
    // }
    //
    // }

}
