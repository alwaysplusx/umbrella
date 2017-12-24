package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.core.ObjectSerializer;

/**
 * @author wuxii@foxmail.com
 */
class NoneObjectSerializer implements ObjectSerializer {

    @Override
    public Object serialize(Object val) {
        return val;
    }

}
