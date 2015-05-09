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
package com.harmony.umbrella.data.repository.support;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleJpaRepository<T, ID extends Serializable> extends AbstractJpaRepository<T, ID> {

	private EntityManager em;
	private Class<T> domainClass;

	public SimpleJpaRepository(EntityManager em, Class<T> domainClass) {
		this.em = em;
		this.domainClass = domainClass;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected Class<T> getDomainClass() {
		return domainClass;
	}

}
