package com.harmony.umbrella.data.query;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.harmony.umbrella.util.StringUtils;

public class DefaultEntityMetadata<T, ID extends Serializable> implements EntityMetadata<T, ID> {

	private final Class<T> domainClass;

	public DefaultEntityMetadata(Class<T> domainClass) {
		this.domainClass = domainClass;
	}

	@Override
	public String getEntityName() {
		Entity entity = domainClass.getAnnotation(Entity.class);
		boolean hasName = null != entity && StringUtils.isNotEmpty(entity.name());
		return hasName ? entity.name() : domainClass.getSimpleName();
	}

	@Override
	public String getTableName() {
		Table table = domainClass.getAnnotation(Table.class);
		boolean hasName = null != table && StringUtils.isNotEmpty(table.name());
		return hasName ? table.name() : getEntityName();
	}

	@Override
	public Class<T> getJavaType() {
		return domainClass;
	}

}
