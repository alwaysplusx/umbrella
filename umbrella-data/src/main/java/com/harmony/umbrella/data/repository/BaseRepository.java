/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.data.repository;

import java.io.Serializable;
import java.util.List;

import com.harmony.umbrella.data.domain.Specification;

/**
 * @author wuxii@foxmail.com
 */
public interface BaseRepository<ID extends Serializable> {

	<S> S save(S entity);

	<S> Iterable<S> save(Iterable<S> entities);

	void delete(Iterable<?> entities);

	void delete(Object entity);

	<S> S delete(Class<S> domainClass, ID id);

	void deleteAll(Class<?> domainClass);

	<S> List<S> findAll(Class<S> domainClass);

	<S> Iterable<S> findAll(Class<S> domainClass, Iterable<ID> ids);

	<S> List<S> findAll(Class<S> domainClass, Specification<S> spec);

	<S> S findOne(Class<S> domainClass, ID id);

	<S> S findOne(Class<S> domainClass, Specification<S> spec);

	boolean exists(Class<?> domainClass, ID id);

	<S> boolean exists(Class<S> domainClass, Specification<S> spec);

	long count(Class<?> domainClass);

	<S> long count(Class<S> domainClass, Specification<S> spec);

}