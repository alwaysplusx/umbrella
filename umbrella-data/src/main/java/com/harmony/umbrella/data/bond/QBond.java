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
package com.harmony.umbrella.data.bond;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class QBond {

    private final String xql;
    private final Map<String, Object> params;

    public QBond(String xql, Map<String, Object> params) {
        Assert.notNull(xql, "xquery must not be null");
        this.xql = xql;
        if (params != null && !params.isEmpty()) {
            this.params = Collections.unmodifiableMap(params);
        } else {
            this.params = Collections.emptyMap();
        }
    }

    public String getXQL() {
        return xql;
    }

    public Iterator<String> paramKeys() {
        return params.keySet().iterator();
    }

    public Object getValue(String key) {
        return params.get(key);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "{" + xql + "\n" + params + "}";
    }

}
