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
package com.harmony.umbrella.data.domain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.harmony.umbrella.util.FieldUtils;
import com.harmony.umbrella.util.MethodUtils;
import com.harmony.umbrella.util.FieldUtils.FieldFilter;
import com.harmony.umbrella.util.MethodUtils.MethodFilter;

/**
 * @author wuxii@foxmail.com
 */
@MappedSuperclass
public abstract class Model<ID extends Serializable> implements Persistable<ID> {

	private static final long serialVersionUID = -9098668260590791573L;

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

	@SuppressWarnings("unchecked")
	@Override
	public ID getId() {
		Method method = getIdMethod();
		if (method != null) {
			try {
				return (ID) method.invoke(this);
			} catch (Exception e) {
			}
		}
		Field idField = getIdField();
		if (idField != null) {
			try {
				String name = idField.getName();
				String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
				method = MethodUtils.findMethod(getClass(), methodName);
				return (ID) MethodUtils.invokeMethod(method, this);
			} catch (Exception e) {
				try {
					if (!idField.isAccessible()) {
						idField.setAccessible(true);
					}
					return (ID) idField.get(this);
				} catch (Exception e1) {
				}
			}
		}
		throw new IllegalStateException("entity " + getClass() + " not mapped @Id @EmbeddedId annotation on field and method");
	}

	@Override
	public boolean isNew() {
		return getId() == null;
	}

	@Transient
	private static final Class<?>[] idClasses = new Class[] { Id.class, EmbeddedId.class };

	/**
	 * 获取主键的方法, 所有声明了的public方法中查找. 标记有{@linkplain #idClasses}其中之一的方法
	 * 
	 * @return 主键的获取方法
	 */
	private Method getIdMethod() {
		return MethodUtils.findMethod(getClass(), new MethodFilter() {
			@Override
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public boolean matches(Method method) {
				for (Class ann : idClasses) {
					if (method.getAnnotation(ann) != null)
						return true;
				}
				return false;
			}
		});
	}

	/**
	 * 获取主键字段, 所有声明的字段中查找. 标记有{@linkplain #idClasses}其中之一的方法
	 * 
	 * @return
	 */
	private Field getIdField() {
		return FieldUtils.findField(getClass(), new FieldFilter() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public boolean matches(Field field) {
				for (Class ann : idClasses) {
					if (field.getAnnotation(ann) != null)
						return true;
				}
				return false;
			}
		});
	}
}
