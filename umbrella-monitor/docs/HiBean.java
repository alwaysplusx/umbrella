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
package com.harmony.umbrella.monitor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import com.harmony.umbrella.monitor.annotation.InternalProperty;
import com.harmony.umbrella.monitor.annotation.Monitor;
import com.harmony.umbrella.monitor.attack.ReflectionAttacker;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "HiBean")
public class HiBean {

    private String name;

    @Interceptors({ LoggingInterceptor.class })
    @Monitor(
        internalProperties = { @InternalProperty(attacker = ReflectionAttacker.class, names = "name") //
    })
    public String sayHi(String name) {
        this.name = name;
        return "Hi " + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
