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
package com.harmony.umbrella.example.ws;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.harmony.umbrella.example.entity.Bar;
import com.harmony.umbrella.example.entity.Foo;
import com.harmony.umbrella.mapper.BeanMapper;
import com.harmony.umbrella.ws.ser.Message;
import com.harmony.umbrella.ws.ser.ServerSupport;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "MapperBean")
@WebService(serviceName = "MapperBean")
public class MapperBean extends ServerSupport {

    public Message mapper(Foo foo) {
        BeanMapper mapper = BeanMapper.getInstance("mapping.xml");
        Bar bar = mapper.mapper(foo, Bar.class, "foo2bar");
        System.out.println(bar);
        return success();
    }

}
