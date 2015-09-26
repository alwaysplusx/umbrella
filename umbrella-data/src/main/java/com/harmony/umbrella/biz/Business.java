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
package com.harmony.umbrella.biz;

import java.io.Serializable;
import java.util.List;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageRequest;

/**
 * @author wuxii@foxmail.com
 */
public interface Business<T extends Model<ID>, ID extends Serializable> {

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    void delete(Iterable<T> entities);

    void deleteById(ID id);

    void deleteByIds(Iterable<ID> ids);

    int delete(Bond bond);

    T findOne(ID id);

    T findOne(Bond bond);

    List<T> findAll();

    List<T> findAll(Bond bond);

    List<T> findAll(Iterable<ID> ids);

    long count(Bond bond);

    long countAll();

    boolean exists(ID id);

    boolean exists(Bond bond);

    boolean isNew(T entity);

    Page<T> page(Bond bond, PageRequest pageRequest);

    Page<T> page(PageRequest pageRequest);
}
