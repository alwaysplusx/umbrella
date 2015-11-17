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
package com.harmony.umbrella.example.ws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.harmony.umbrella.ws.ser.Message;

/**
 * @author wuxii@foxmail.com
 */
@WebService(targetNamespace = "http://www.harmony.com/srm/test/user")
public interface UserService {

    @WebResult(name = "result")
    Message saveOrUpdateUser(@WebParam(name = "users") List<User> users);

}
