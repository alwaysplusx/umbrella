package com.harmony.umbrella.web.bind.annotation;

import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author wuxii@foxmail.com
 */
public @interface ResponseBundle {

    String[] excludes() default {};

    String[] includes() default {};

    SerializerFeature[] features() default {};

}
