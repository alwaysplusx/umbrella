package com.harmony.umbrella.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;

/**
 * {@linkplain Dao}的JPA扩展接口
 * 
 * @author wuxii@foxmail.com
 * @param <T>
 *            处理的entity类
 * @param <ID>
 *            entity的id类型
 */
public interface JpaDao<T, ID extends Serializable> extends Dao {

    <E> EntityMetadata<E, ?> getEntityMetadata(Class<E> entityClass);

    /**
     * 保存entity, 并刷新context
     * 
     * @param entity
     *            带保存的entity
     * @return 保存后的entity
     * @see #flush()
     */
    T saveAndFlush(T entity);

    /**
     * 根据约束条件删除数据
     * 
     * @param spec
     *            删除的约束条件
     * @return 删除的条数
     */
    int delete(Specification<T> spec);

    /**
     * 删除所有entity
     */
    void deleteAll();

    /**
     * 根据id删除
     * 
     * @param id
     *            主键id
     */
    void deleteById(ID id);

    /**
     * 根据id批量删除
     * 
     * @param ids
     *            entity的主键id
     */
    void deleteByIds(Iterable<ID> ids);

    /**
     * 批量删除entity
     * 
     * @param entities
     *            待删除的entity
     */
    void deleteInBatch(Iterable<T> entities);

    /**
     * 删除所有entity
     */
    void deleteAllInBatch();

    /**
     * 通过id查询entity
     * 
     * @param id
     *            entity的id
     * @return entity object if not find return null
     */
    T findById(ID id);

    /**
     * 带排序条件的查询所有数据
     * 
     * @param sort
     *            排序条件
     * @return 所有entity数据
     */
    Iterable<T> findAll(Sort sort);

    /**
     * 查询所有entity
     * 
     * @return entity的所有数据, or empty list
     */
    List<T> findAll();

    /**
     * 查询出对应id的entity
     * 
     * @param ids
     *            指定的id
     * @return id按顺序对应, 如果有id未找到则该index为null
     */
    List<T> findAll(Iterable<ID> ids);

    /**
     * Synchronize the persistence context to the underlying database.
     * 
     * @see EntityManager#flush()
     */
    void flush();

    /**
     * 统计entity的数据量
     * 
     * @return 数据条数
     */
    long count();

    /**
     * Returns the number of instances that the given {@link Specification} will
     * return.
     * 
     * @param spec
     *            the {@link Specification} to count instances for
     * @return the number of instances
     */
    long count(Specification<T> spec);

    /**
     * 通过id验证是否存在entity
     * 
     * @param id
     *            指定的id
     * @return true is exist
     */
    boolean exists(ID id);

    /**
     * 通过查询条件验证是否存在entity
     * 
     * @param spec
     *            规范化查询条件
     * @return if exist return true
     */
    boolean exists(Specification<T> spec);

    /**
     * 使用sql查询entity
     * 
     * @param sql
     *            sql语句
     * @return entity
     * @see #findOneBySQL(String, Class)
     */
    T findOneBySQL(String sql);

    /**
     * 带参数sql查询entity
     * 
     * @param sql
     *            sql语句
     * @param parameters
     *            查询参数
     * @return entity
     * @see #findOneBySQL(String, Class, Map)
     */
    T findOneBySQL(String sql, Map<String, Object> parameters);

    /**
     * 带参数sql查询entity
     * 
     * @param sql
     *            sql语句
     * @param parameters
     *            查询参数
     * @return entity
     * @see #findOneBySQL(String, Class, Object...)
     */
    T findOneBySQL(String sql, Object... parameters);

    /**
     * 使用sql查询符合条件的entity
     * 
     * @param sql
     *            sql语句
     * @return 符合条件的数据
     * @see #findAllBySQL(String, Class)
     */
    List<T> findAllBySQL(String sql);

    /**
     * 带参数sql查询entity
     * 
     * @param sql
     *            sql语句
     * @param parameters
     *            查询参数
     * @return 符合条件的数据
     * @see #findAllBySQL(String, Class, Map)
     */
    List<T> findAllBySQL(String sql, Map<String, Object> parameters);

    /**
     * 带参数sql查询entity
     * 
     * @param sql
     *            sql语句
     * @param parameters
     *            查询参数
     * @return 符合条件的数据
     * @see #findAllBySQL(String, Class, Object)
     */
    List<T> findAllBySQL(String sql, Object... parameters);

    /**
     * Returns a reference to the entity with the given identifier.
     * 
     * @param id
     *            must not be {@literal null}.
     * @return a reference to the entity with the given identifier.
     * @see EntityManager#getReference(Class, Object)
     */
    T getOne(ID id);

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
     * Returns all entities matching the given {@link Specification} and
     * {@link Sort}.
     * 
     * @param spec
     * @param sort
     * @return
     */
    List<T> findAll(Specification<T> spec, Sort sort);

    /**
     * 带分页的查询所有数据
     * 
     * @param pageable
     *            分页条件
     * @return 分页后的entity数据
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Returns a {@link Page} of entities matching the given
     * {@link Specification}.
     * 
     * @param spec
     * @param pageable
     * @return
     */
    Page<T> findAll(Specification<T> spec, Pageable pageable);
}
