package com.harmony.umbrella.web.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryFeature;
import com.harmony.umbrella.data.service.Service;
import com.harmony.umbrella.web.method.annotation.BundleQuery;

/**
 * FIXME BundleQuery泛型选择会有问题
 * 
 * @author wuxii@foxmail.com
 */
public abstract class BaseController<T, ID extends Serializable> {

    protected abstract Service<T, ID> getService();

    @RequestMapping({ "/add", "/save" })
    public T save(T model) {
        return getService().saveOrUpdate(model);
    }

    @RequestMapping("/update")
    public T update(T model) {
        return getService().saveOrUpdate(model);
    }

    @RequestMapping("/view/{id}")
    public T view(//
            @PathVariable(name = "id", required = false) ID pathVarId, //
            @RequestParam(name = "id", required = false) ID paramId) {
        ID id = pathVarId;
        if (id == null) {
            id = paramId;
        }
        return getService().findOne(id);
    }

    @RequestMapping("/page/{no}")
    public Page<T> page(JpaQueryBuilder<T> queryBuilder, //
            @PathVariable(name = "no", required = false) Integer pageNo, //
            @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (pageNo != null) {
            queryBuilder.paging(pageNo, size);
        }
        return getService().findPage(queryBuilder.bundle());
    }

    @RequestMapping("/list")
    @BundleQuery(feature = { QueryFeature.FULL_TABLE_QUERY })
    public List<T> list(QueryBundle<T> bundle) {
        return getService().findList(bundle);
    }

    @RequestMapping("/delete/{id}")
    public Response delete(//
            @PathVariable(name = "id", required = false) ID pathVarId, //
            @RequestParam(name = "id", required = false) ID paramId, //
            @RequestParam(name = "ids", required = false) ID[] ids) {
        return null;
    }

}
