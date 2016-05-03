package com.harmony.umbrella.ws.ext;

import java.io.Serializable;
import java.util.Map;

import com.harmony.umbrella.biz.Business;
import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.ws.proxy.Proxy;

/**
 * @author wuxii@foxmail.com
 */
public interface ProxyBusiness<T extends Model<ID>, ID extends Serializable> extends Proxy<T>, Business<T, ID> {

    boolean syncById(ID id);

    boolean syncById(ID id, Map<String, Object> properties);

    boolean syncInBatchById(Iterable<ID> ids);

    boolean syncInBatchById(Iterable<ID> ids, Map<String, Object> properties);

}
