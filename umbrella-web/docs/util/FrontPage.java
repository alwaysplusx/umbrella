package com.harmony.umbrella.web.util;

import java.util.Collection;

import com.alibaba.fastjson.annotation.JSONField;
import com.harmony.umbrella.data.domain.Page;

/**
 * @author wuxii@foxmail.com
 */
public class FrontPage {

    private final Page<?> page;

    public FrontPage(Page<?> page) {
        this.page = page;
    }

    @JSONField(ordinal = 0)
    public long getTotalCount() {
        return page.getTotalElements();
    }

    @JSONField(ordinal = 1)
    public Collection<?> getRecords() {
        return page.getContent();
    }

}
