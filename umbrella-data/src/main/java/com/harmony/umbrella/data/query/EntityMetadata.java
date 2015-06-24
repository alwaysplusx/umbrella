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
package com.harmony.umbrella.data.query;

import java.io.Serializable;

/**
 * JPA specific extension of {@link EntityMetadata}.
 * <p>一个Entity对应一个元数据<p>基础元数据有<li>Entity的名称<li>Entity的java类型
 * 
 * @author Oliver Gierke
 */
public interface EntityMetadata<T, ID extends Serializable> {

	/**
	 * Returns the name of the entity.
	 * 
	 * @return
	 */
	String getEntityName();

	/**
	 * Returns the table name of entity
	 * 
	 * @return
	 */
	String getTableName();

	/**
	 * Returns the actual domain class type.
	 * 
	 * @return
	 */
	Class<T> getJavaType();

}
