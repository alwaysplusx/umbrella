package com.harmony.modules.data.query;

/**
 * JPA specific extension of {@link EntityMetadata}.
 * <p>一个Entity对应一个元数据，基础元数据有Entity的名称，以及Entity的java类型
 * 
 * @author Oliver Gierke
 */
public interface EntityMetadata<T> {

	/**
	 * Returns the name of the entity.
	 * 
	 * @return
	 */
	String getEntityName();

	/**
	 * Returns the actual domain class type.
	 * 
	 * @return
	 */
	Class<T> getJavaType();
}
