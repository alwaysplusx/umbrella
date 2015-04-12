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
package com.harmony.modules.data.repository.support;

import java.io.Serializable;
import java.util.List;

import com.harmony.modules.data.domain.Page;
import com.harmony.modules.data.domain.Sort;
import com.harmony.modules.data.domain.Specification;
import com.harmony.modules.data.repository.JpaRepository;
import com.harmony.modules.data.repository.JpaSpecificationExecutor;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleJpaRepository<T, ID extends Serializable> implements JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	@Override
	public Page<T> findAll(Page<T> pageable) {
		return null;
	}

	@Override
	public <S extends T> S save(S entity) {
		return null;
	}

	@Override
	public T findOne(ID id) {
		return null;
	}

	@Override
	public boolean exists(ID id) {
		return false;
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public T delete(ID id) {
		return null;
	}

	@Override
	public void delete(T entity) {
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public T findOne(Specification<T> spec) {
		return null;
	}

	@Override
	public List<T> findAll(Specification<T> spec) {
		return null;
	}

	@Override
	public Page<T> findAll(Specification<T> spec, Page<T> pageable) {
		return null;
	}

	@Override
	public List<T> findAll(Specification<T> spec, Sort sort) {
		return null;
	}

	@Override
	public long count(Specification<T> spec) {
		return 0;
	}

	@Override
	public List<T> findAll() {
		return null;
	}

	@Override
	public List<T> findAll(Sort sort) {
		return null;
	}

	@Override
	public List<T> findAll(Iterable<ID> ids) {
		return null;
	}

	@Override
	public <S extends T> List<S> save(Iterable<S> entities) {
		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public <S extends T> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public void deleteInBatch(Iterable<T> entities) {
	}

	@Override
	public void deleteAllInBatch() {
	}

	@Override
	public T getOne(ID id) {
		return null;
	}
}
