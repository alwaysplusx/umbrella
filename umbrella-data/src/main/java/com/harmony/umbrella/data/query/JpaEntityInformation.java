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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.IdClass;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import com.harmony.umbrella.util.FieldUtils;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * Jpa的Entity Information
 * 
 * @author wuxii@foxmail.com
 */
public class JpaEntityInformation<T, ID extends Serializable> extends DefaultEntityMetadata<T, ID> implements EntityInformation<T, ID> {

    protected final Metamodel metamodel;
    private final String entityName;
    private final IdentifiableType<T> identifiableType;
    private Set<SingularAttribute<? super T, ?>> idAttributes;

    public JpaEntityInformation(Class<T> domainClass, Metamodel metamodel) {
        super(domainClass);
        ManagedType<T> type = metamodel.managedType(domainClass);
        if (type == null) {
            throw new IllegalArgumentException("The given domain class can not be found in the given Metamodel!");
        }
        if (!(type instanceof IdentifiableType))
            throw new IllegalArgumentException("The given domain class does not contain an id attribute!");
        this.identifiableType = (IdentifiableType<T>) type;
        this.metamodel = metamodel;
        this.entityName = type instanceof EntityType ? ((EntityType<T>) type).getName() : null;
    }

    @Override
    public String getEntityName() {
        return entityName == null ? super.getEntityName() : entityName;
    }

    @Override
    public boolean isNew(T entity) {
        return getId(entity) == null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ID getId(T entity) {
        if (identifiableType.hasSingleIdAttribute()) {
            SingularAttribute<? super T, ?> idAttr = identifiableType.getId(identifiableType.getIdType().getJavaType());
            return (ID) FieldUtils.getFieldValue(idAttr.getName(), entity);
        }
        try {
            boolean partialIdValueFound = false;
            ID id = ReflectionUtils.instantiateClass(getIdType());
            for (SingularAttribute<? super T, ?> attr : getIdAttributes()) {
                Object value = FieldUtils.getFieldValue(attr.getName(), entity);
                if (value != null) {
                    partialIdValueFound = true;
                }
                FieldUtils.setFieldValue(attr.getName(), id, value);
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
    public Object getCompositeIdAttributeValue(Serializable id, String idAttribute) {
        if (!hasCompositeId())
            throw new IllegalArgumentException("not composite id entity");
        return FieldUtils.getFieldValue(idAttribute, id);
    }

}
