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
package com.harmony.umbrella.monitor.annotation;

/**
 * @author wuxii@foxmail.com
 */
public enum Mode {
    /**
     * 请求
     */
    IN,
    /**
     * 应答
     */
    OUT,
    /**
     * 请求+应答
     */
    INOUT;

    public boolean inRange(Mode mode) {
        return this == mode || INOUT == mode;
    }
}
