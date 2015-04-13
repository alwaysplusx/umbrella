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
