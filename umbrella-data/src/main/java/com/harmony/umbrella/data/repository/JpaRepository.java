package com.harmony.umbrella.data.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.domain.Sort;

public interface JpaRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
	
	long count();

	void deleteAll();

	List<T> findAll();

	List<T> findAll(Sort sort);

	List<T> findAll(Iterable<ID> ids);

	<S extends T> List<S> save(Iterable<S> entities);

	void flush();

	<S extends T> S saveAndFlush(S entity);

	void deleteInBatch(Iterable<T> entities);

	void deleteAllInBatch();

	/**
	 * Returns a reference to the entity with the given identifier.
	 * 
	 * @param id
	 *            must not be {@literal null}.
	 * @return a reference to the entity with the given identifier.
	 * @see EntityManager#getReference(Class, Object)
	 */
	T getOne(ID id);
}
