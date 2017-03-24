package com.harmony.umbrella.data.repository;

import org.springframework.stereotype.Repository;

import com.harmony.umbrella.data.entity.Model;

/**
 * @author wuxii@foxmail.com
 */
@Repository
public interface ModelRepository extends QueryableRepository<Model, Long> {

}
