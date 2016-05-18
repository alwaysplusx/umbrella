package com.harmony.umbrella.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author copy from spring
 */
public class DefaultEntityMetadata<T, ID extends Serializable> implements EntityMetadata<T, ID> {

    protected final Class<T> entityClass;
    protected final Metamodel metamodel;
    protected final IdentifiableType<T> identifiableType;

    private String tableName;
    private String entityName;
    private Set<SingularAttribute<? super T, ?>> idAttributes;

    public DefaultEntityMetadata(Class<T> entityClass, Metamodel metamodel) {
        this.entityClass = entityClass;
        this.metamodel = metamodel;
        ManagedType<T> type = metamodel.managedType(entityClass);
        if (type == null) {
            throw new IllegalArgumentException("The given domain class can not be found in the given Metamodel!");
        }
        if (!(type instanceof IdentifiableType)) {
            throw new IllegalArgumentException("The given domain class does not contain an id attribute!");
        }
        this.identifiableType = (IdentifiableType<T>) type;
        this.entityName = type instanceof EntityType ? ((EntityType<T>) type).getName() : null;
    }

    @Override
    public String getEntityName() {
        if (entityName == null) {
            Entity entity = entityClass.getAnnotation(Entity.class);
            entityName = (null != entity && StringUtils.isNotEmpty(entity.name())) ? entity.name() : entityClass.getSimpleName();
        }
        return entityName;
    }

    @Override
    public String getTableName() {
        if (tableName == null) {
            Table table = entityClass.getAnnotation(Table.class);
            return (null != table && StringUtils.isNotEmpty(table.name())) ? table.name() : getEntityName();
        }
        return tableName;
    }

    @Override
    public Class<T> getJavaType() {
        return entityClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ID getId(T entity) {
        if (identifiableType.hasSingleIdAttribute()) {
            SingularAttribute<? super T, ?> idAttr = identifiableType.getId(identifiableType.getIdType().getJavaType());
            return (ID) ReflectionUtils.getFieldValue(idAttr.getName(), entity);
        }
        try {
            boolean partialIdValueFound = false;
            ID id = ReflectionUtils.instantiateClass(getIdType());
            for (SingularAttribute<? super T, ?> attr : getIdAttributes()) {
                Object value = ReflectionUtils.getFieldValue(attr.getName(), entity);
                if (value != null) {
                    partialIdValueFound = true;
                }
                ReflectionUtils.setFieldValue(attr.getName(), id, value);
            }
            return partialIdValueFound ? id : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("can't new instance object", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<ID> getIdType() {
        if (identifiableType.getIdType() == null) {
            IdClass annotation = identifiableType.getJavaType().getAnnotation(IdClass.class);
            return annotation.value();
        }
        return (Class<ID>) identifiableType.getIdType().getJavaType();
    }

    @Override
    public SingularAttribute<? super T, ?> getIdAttribute() {
        return getIdAttributes().iterator().next();
    }

    @Override
    public boolean hasCompositeId() {
        return getIdAttributes().size() > 1;
    }

    @Override
    public Iterable<String> getIdAttributeNames() {
        Set<SingularAttribute<? super T, ?>> attributes = getIdAttributes();
        List<String> attrNames = new ArrayList<String>();
        for (SingularAttribute<? super T, ?> attr : attributes) {
            attrNames.add(attr.getName());
        }
        return attrNames;
    }

    protected Set<SingularAttribute<? super T, ?>> getIdAttributes() {
        if (idAttributes == null) {
            if (identifiableType.hasSingleIdAttribute()) {
                idAttributes = Collections.<SingularAttribute<? super T, ?>> singleton(identifiableType.getId(getIdType()));
            } else {
                idAttributes = identifiableType.getIdClassAttributes();
            }
        }
        return idAttributes;
    }

    @Override
    public boolean isNew(T entity) {
        return getId(entity) == null;
    }

    @Override
    public Object getCompositeIdAttributeValue(Serializable id, String idAttribute) {
        if (!hasCompositeId()) {
            throw new IllegalArgumentException("not composite id entity");
        }
        return ReflectionUtils.getFieldValue(idAttribute, id);
    }

}
