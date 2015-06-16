package com.harmony.umbrella.data.repository;

import java.io.Serializable;

/**
 * 持久化与基础查询类
 * 
 * @author wuxii
 *
 */
public interface Repository<T, ID extends Serializable> {

	<S extends T> S save(S entity);

	<S extends T> Iterable<S> save(Iterable<S> entities);

	T findOne(ID id);

	boolean exists(ID id);

	Iterable<T> findAll(Iterable<ID> ids);

	T delete(ID id);

	void delete(T entity);

	void delete(Iterable<? extends T> entities);

}
