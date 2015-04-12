package com.harmony.modules.data.query;

import java.io.Serializable;

import javax.persistence.metamodel.SingularAttribute;

/**
 * Extension of {@link EntityMetadata} to add functionality to query information of entity
 * instances.
 * 
 * @author Oliver Gierke
 */
public interface EntityInformation<T, ID extends Serializable> extends EntityMetadata<T, ID> {

	/**
	 * Returns whether the given entity is considered to be new.
	 * 
	 * @param entity
	 *            must never be {@literal null}
	 * @return
	 */
	boolean isNew(T entity);

	/**
	 * Returns the id of the given entity.
	 * 
	 * @param entity
	 *            must never be {@literal null}
	 * @return
	 */
	ID getId(T entity);

	/**
	 * Returns the type of the id of the entity.
	 * 
	 * @return
	 */
	Class<ID> getIdType();

	/**
	 * Returns the id attribute of the entity.
	 * 
	 * @return
	 */
	SingularAttribute<? super T, ?> getIdAttribute();

	/**
	 * Returns {@literal true} if the entity has a composite id.
	 * 
	 * @return
	 */
	boolean hasCompositeId();

	/**
	 * Returns the attribute names of the id attributes. If the entity has a composite id,
	 * then all id attribute names are returned. If the entity has a single id attribute
	 * then this single attribute name is returned.
	 * 
	 * @return
	 */
	Iterable<String> getIdAttributeNames();

	/**
	 * Extracts the value for the given id attribute from a composite id
	 * 
	 * @param id
	 * @param idAttribute
	 * @return
	 */
	Object getCompositeIdAttributeValue(Serializable id, String idAttribute);
}
