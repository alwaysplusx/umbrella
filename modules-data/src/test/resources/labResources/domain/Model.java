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
package com.harmony.modules.data.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.utils.MethodCaller;

/**
 * @author wuxii@foxmail.com
 */
@MappedSuperclass
public abstract class Model {

    protected static final Logger log = LoggerFactory.getLogger(Model.class);

    @Column(updatable = false)
    protected Long createUserId;

    @Column(updatable = false)
    protected String createUserName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    protected Calendar createTime;

    protected Long modifyUserId;

    protected String modifyUserName;

    @Temporal(TemporalType.TIMESTAMP)
    protected Calendar modifyTime;

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public Calendar getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Calendar modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idValue() == null) ? 0 : idValue().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return  getClass().getName() + ":{\"id\":\"" + idValue() + "\"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Model other = (Model) obj;
        if (idValue() == null) {
            if (other.idValue() != null)
                return false;
        } else if (!idValue().equals(other.idValue()))
            return false;
        return true;
    }

    @Transient
    private static final Class<?>[] idClasses = new Class[] { Id.class, EmbeddedId.class };

    /**
     * 获取主键的方法, 所有声明了的public方法中查找. 标记有{@linkplain #idClasses}其中之一的方法
     * @return 主键的获取方法
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Method getIdMethod() {
        for (Method method : getClass().getMethods()) {
            for (Class c : idClasses) {
                if (method.getAnnotation(c) != null) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获取主键字段, 所有声明的字段中查找. 标记有{@linkplain #idClasses}其中之一的方法
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Field getIdField() {
        for (Field field : getClass().getDeclaredFields()) {
            for (Class c : idClasses) {
                if (field.getAnnotation(c) != null) {
                    return field;
                }
            }
        }
        return null;
    }

    public Object idValue() {
        Method method = getIdMethod();
        if (method != null) {
            try {
                return method.invoke(this);
            } catch (Exception e) {
                // throw new IllegalStateException("can't access entity " +
                // getClass() + "#" + method.getName() + " method", e);
            }
        }
        Field idField = getIdField();
        if (idField != null) {
            try {
                return MethodCaller.invokeFieldGetMethod(this, idField);
            } catch (Exception e) {
                try {
                    if (!idField.isAccessible()) {
                        idField.setAccessible(true);
                    }
                    return idField.get(this);
                } catch (Exception e1) {
                    // throw new IllegalStateException("can't access entity " +
                    // getClass() + "#" + idField.getName() + " field");
                }
            }
        }
        // throw new IllegalStateException("entity " + getClass() +
        // " not mapped @Id @EmbeddedId annotation on field and method");
        return null;
    }

    public String idName() {
        Field idField = getIdField();
        if (idField != null)
            return idField.getName();
        Method idMethod = getIdMethod();
        if (idMethod != null) {
            String methodName = idMethod.getName();
            if (methodName.startsWith("get")) {
                char firstChar = idMethod.getName().substring(3).charAt(0);
                String suffix = "";
                if (methodName.length() > 4) {
                    suffix = methodName.substring(4);
                }
                try {
                    idField = getClass().getField(Character.toLowerCase(firstChar) + suffix);
                    return idField.getName();
                } catch (NoSuchFieldException e) {
                    try {
                        idField = getClass().getField(firstChar + suffix);
                        return idField.getName();
                    } catch (NoSuchFieldException e1) {
                        // throw new
                        // IllegalArgumentException("illegal id get method name "
                        // + idMethod.getName() +
                        // ", use method name like get[FieldName]");
                    }
                }
            }
        }
        // throw new IllegalStateException("entity " + getClass() +
        // " not mapped @Id @EmbeddedId annotation on field and method");
        return null;
    }

    public String entityName() {
        try {
            Entity ann = getClass().getAnnotation(Entity.class);
            if (!"".equals(ann.name())) {
                return ann.name();
            }
        } catch (Exception e) {
            log.warn("", e);
        }
        return getClass().getSimpleName();
    }

    public String tableName() {
        try {
            Table ann = getClass().getAnnotation(Table.class);
            if (ann != null && !"".equals(ann.name())) {
                return ann.name();
            }
        } catch (Exception e) {
            log.warn("", e);
        }
        return entityName();
    }
}
