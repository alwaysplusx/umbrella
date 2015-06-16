package com.harmony.umbrella.data.repository;

import java.util.List;

import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.domain.Specification;

public interface JpaSpecificationExecutor<T> {

	/**
	 * Returns a single entity matching the given {@link Specification}.
	 * 
	 * @param spec
	 * @return
	 */
	T findOne(Specification<T> spec);

	/**
	 * Returns all entities matching the given {@link Specification}.
	 * 
	 * @param spec
	 * @return
	 */
	List<T> findAll(Specification<T> spec);

	/**
	 * Returns a {@link Page} of entities matching the given
	 * {@link Specification}.
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	Page<T> findAll(Specification<T> spec, Pageable pageable);

	/**
	 * Returns all entities matching the given {@link Specification} and
	 * {@link Sort}.
	 * 
	 * @param spec
	 * @param sort
	 * @return
	 */
	List<T> findAll(Specification<T> spec, Sort sort);

	/**
	 * Returns the number of instances that the given {@link Specification} will
	 * return.
	 * 
	 * @param spec
	 *            the {@link Specification} to count instances for
	 * @return the number of instances
	 */
	long count(Specification<T> spec);
}