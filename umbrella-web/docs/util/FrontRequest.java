package com.harmony.umbrella.web.util;

import com.harmony.umbrella.data.domain.PageRequest;

/**
 * @author wuxii@foxmail.com
 */
public class FrontRequest extends PageRequest {

    private static final long serialVersionUID = 1L;

    public FrontRequest() {
        this(1, 20);
    }

    public FrontRequest(int page, int size) {
        super(page, size);
    }

    public void setPage(int page) {
        setPageNumer(page);
    }

    public void setLimit(int limit) {
        setPageSize(limit);
    }

    public int getPage() {
        return getPageNumber();
    }

    public int getLimit() {
        return getPageSize();
    }

    @Override
    public int getOffset() {
        return (getPageNumber() - 1) * getPageSize();
    }
}
