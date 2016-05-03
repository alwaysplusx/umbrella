package com.harmony.umbrella.mapper.metadata.impl;

import java.lang.reflect.Method;

import com.harmony.umbrella.mapper.metadata.ClassMappable;
import com.harmony.umbrella.mapper.metadata.RowMappable;

/**
 * @author wuxii@foxmail.com
 */
public class RowMappableImpl implements RowMappable {

	private final ClassMappable owner;
	private Method readMethod;
	private Method writeMethod;
	private String name;

	public RowMappableImpl(Method readMethod, Method writeMethod, ClassMappable owner) {
		this.owner = owner;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
	}

	/**
	 * 根据字段名来创建{@linkplain RowMappable}
	 * 
	 * @param name
	 *            字段名称
	 * @param owner
	 */
	public RowMappableImpl(String name, ClassMappable owner) {
		this.owner = owner;
	}

	@Override
	public String getMappedName() {
		if (name == null) {
			String temp = readMethod == null ? writeMethod.getName().substring(3) : readMethod.getName().substring(3);
			name = String.valueOf(temp.charAt(0)).toLowerCase() + temp.substring(1);
		}
		return name;
	}

	@Override
	public Class<?> getMappedClass() {
		return owner.getMappedClass();
	}

	@Override
	public Method getReadMethod() {
		return readMethod;
	}

	@Override
	public Method getWriteMethod() {
		return writeMethod;
	}

	@Override
	public Class<?> getType() {
		return readable() ? readMethod.getReturnType() : null;
	}

	@Override
	public boolean readable() {
		return readMethod != null;
	}

	@Override
	public boolean writable() {
		return writeMethod != null;
	}

	@Override
	public ClassMappable getOwner() {
		return owner;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{")
			.append("name:").append(getMappedName()).append(", ")
			.append("readable:").append(readable()).append(", ")
			.append("writable:").append(writable()).append(", ")
			.append("type:").append(getType().getName())
		.append("}");
		return sb.toString();
	}

}
