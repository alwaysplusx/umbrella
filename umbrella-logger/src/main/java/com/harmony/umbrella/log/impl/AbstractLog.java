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
package com.harmony.umbrella.log.impl;

import com.harmony.umbrella.log.Log;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractLog implements Log {

    private final String className;

    protected boolean isTraceEnabled = false;
    protected boolean isDebugEnabled = false;
    protected boolean isInfoEnabled = false;
    protected boolean isWarnEnabled = true;
    protected boolean isErrorEnabled = true;

    public AbstractLog(String className) {
        this.className = className;
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public boolean isTraceEnabled() {
        return isTraceEnabled;
    }

    @Override
    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public boolean isInfoEnabled() {
        return isInfoEnabled;
    }

    @Override
    public boolean isWarnEnabled() {
        return isWarnEnabled;
    }

    @Override
    public boolean isErrorEnabled() {
        return isErrorEnabled;
    }

}
