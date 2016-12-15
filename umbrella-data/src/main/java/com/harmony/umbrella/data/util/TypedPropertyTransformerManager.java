package com.harmony.umbrella.data.util;

import java.util.Collection;

import com.harmony.umbrella.data.domain.Page;

/**
 * @author wuxii@foxmail.com
 */
public class TypedPropertyTransformerManager {

    public static final TypedPropertyTransformer pagePropertyTransformer = new PagePropertyTransformer();

    public static final TypedPropertyTransformer arrayPropertyTransformer = new ArrayPropertyTransformer();

    public static TypedPropertyTransformer[] getDefaultTransformer() {
        return new TypedPropertyTransformer[] { pagePropertyTransformer, arrayPropertyTransformer };
    }

}

class PagePropertyTransformer extends AbstractPropertyTransformer {

    @Override
    public boolean support(Class<?> type) {
        return Page.class.isAssignableFrom(type);
    }

    @Override
    public String getPrefix() {
        return "content[*].";
    }

}

class ArrayPropertyTransformer extends AbstractPropertyTransformer {

    @Override
    public boolean support(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    @Override
    public String getPrefix() {
        return "[*].";
    }

}