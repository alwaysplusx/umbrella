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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuxii@foxmail.com
 */
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

	@Transient
	private static final Class<?>[] idClasses = new Class[] { Id.class, EmbeddedId.class };

	/**
	 * 获取主键的方法
	 * 
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
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			throw new IllegalStateException("can't access entity " + getClass() + "#" + method.getName() + " method");
		}
		Field idField = getIdField();
		if (idField != null) {
			String fieldName = idField.getName();
			try {
				Method fieldValueGetMethod = getClass().getMethod("get" + Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1));
				return fieldValueGetMethod.invoke(this);
			} catch (NoSuchMethodException e) {
				idField.setAccessible(true);
				try {
					return idField.get(this);
				} catch (IllegalAccessException e1) {
				}
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			throw new IllegalStateException("can't access entity " + getClass() + "#" + fieldName + " field");
		}
		throw new IllegalStateException("entity " + getClass() + " not mapped @Id @EmbeddedId annotation on field and method");
	}

	public String idName() {
		Field idField = getIdField();
		if (idField == null) {
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
						}
					}
				}
				throw new IllegalArgumentException("illegal id get method name " + idMethod.getName() + ", use method name like get[FieldName]");
			}
		}
		throw new IllegalStateException("entity " + getClass() + " not mapped @Id @EmbeddedId annotation on field and method");
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
