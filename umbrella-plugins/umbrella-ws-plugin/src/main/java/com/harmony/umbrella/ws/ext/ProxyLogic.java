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
package com.harmony.umbrella.ws.ext;

import java.io.Serializable;
import java.util.Map;

import com.harmony.modules.commons.data.BaseEntity;
import com.harmony.modules.commons.support.GenericLogic;
import com.harmony.umbrella.ws.proxy.Proxy;

/**
 * 同步支持harmony-modules的扩展支持
 * 
 * @author wuxii@foxmail.com
 */
public interface ProxyLogic<T extends BaseEntity<ID>, ID extends Serializable> extends Proxy<T>, GenericLogic<T, ID> {

    boolean syncById(ID id);

    boolean syncById(ID id, Map<String, Object> properties);

    boolean syncInBatchById(Iterable<ID> ids);

    boolean syncInBatchById(Iterable<ID> ids, Map<String, Object> properties);
}
