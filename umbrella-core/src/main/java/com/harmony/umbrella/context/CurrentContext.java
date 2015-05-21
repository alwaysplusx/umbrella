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
package com.harmony.umbrella.context;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * @author wuxii@foxmail.com
 */
public interface CurrentContext extends Serializable {

    String REMOTE_HOST = CurrentContext.class.getName() + ".REMOTE_HOST";

    String USER_ID = CurrentContext.class.getName() + ".USER_ID";

    String USERNAME = CurrentContext.class.getName() + ".CURRENT_USERNAME";

    String HTTP_REQUEST = CurrentContext.class.getName() + ".HTTP_REQUEST";

    String HTTP_RESPONSE = CurrentContext.class.getName() + ".HTTP_RESPONSE";

    String HTTP_SESSION = CurrentContext.class.getName() + ".HTTP_SESSION";

    Long getUserId();

    String getUsername();

    String getRemoteHost();

    boolean isHttpContext();

    boolean contains(String name);

    <T> T get(String name);

    void put(String name, Object o);

    Enumeration<String> getCurrentNames();

}
