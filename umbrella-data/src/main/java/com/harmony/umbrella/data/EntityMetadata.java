package com.harmony.umbrella.data;

import java.io.Serializable;

import javax.persistence.metamodel.SingularAttribute;

/**
 * JPA specific extension of {@link EntityMetadata}.
 * <p>
 * 一个Entity对应一个元数据
 * <p>
 * 基础元数据有
 * <li>Entity的名称
 * <li>Entity的java类型
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
     * Returns the attribute names of the id attributes. If the entity has a
     * composite id, then all id attribute names are returned. If the entity has
     * a single id attribute then this single attribute name is returned.
     * 
     * @return
     */
    Iterable<String> getIdAttributeNames();

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
     * Extracts the value for the given id attribute from a composite id
     * 
     * @param id
     * @param idAttribute
     * @return
     */
    Object getCompositeIdAttributeValue(Serializable id, String idAttribute);
}
