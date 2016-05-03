package com.harmony.umbrella.mapper.metadata.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.mapper.metadata.ClassMappable;
import com.harmony.umbrella.mapper.metadata.RowMappable;

/**
 * @author wuxii@foxmail.com
 */
public class ClassMappableImpl implements ClassMappable {

	private final Class<?> mappedClass;
	private Set<RowMappable> rows = new HashSet<RowMappable>();

	// private Map<Method, Boolean> methods = new HashMap<Method, Boolean>();

	public ClassMappableImpl(Class<?> mappedClass) {
		this.mappedClass = mappedClass;
		/*
		 * for (Method method : mappedClass.getMethods()) { methods.put(method,
		 * Boolean.FALSE); }
		 */
	}

	@Override
	public Class<?> getMappedClass() {
		return mappedClass;
	}

	@Override
	public String getMappedName() {
		return mappedClass.getName();
	}

	@Override
	public Set<RowMappable> getRowMappables() {
		if (rows.isEmpty()) {
			Method[] methods = mappedClass.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("get") && method.getName().length() > 3) {
					String name = method.getName().substring(3);
					for (Method method2 : methods) {
						if (method2.getName().startsWith("set") && method2.getName().endsWith(name)) {
							rows.add(new RowMappableImpl(method, method2, this));
						}
					}
				}
			}
		}
		return rows;
	}

	// protected void buildRowMap() {
	// Iterator<Entry<Method, Boolean>> iterator = methods.entrySet().iterator();
	// for (; iterator.hasNext();) {
	// Entry<Method, Boolean> entry = iterator.next();
	// Method method = entry.getKey();
	// Boolean value = entry.getValue();
	// if (!Boolean.valueOf(value)) {
	// if (Object.class == method.getDeclaringClass()) {
	// methods.put(method, Boolean.TRUE);
	// continue;
	// }
	// }
	// }
	// }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mappedClass.getSimpleName()).append("->").append("{\n");
		int i = 1;
		for (RowMappable row : getRowMappables()) {
			sb.append("  row_").append(i++).append(":").append(row).append("\n");
		}
		sb.append("}");
		return sb.toString();
	}
}
