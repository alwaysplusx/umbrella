package com.harmony.umbrella.data.result;

import org.springframework.data.domain.Page;
import org.springframework.data.util.Streamable;

import java.util.Iterator;

/**
 * @author wuxii
 */
public class PageResult implements Streamable<RowResult> {

    private final Page<RowResult> rowPage;

    public PageResult(Page<RowResult> rowPage) {
        this.rowPage = rowPage;
    }

    public <T> Page<T> toPage(Class<T> resultClass) {
        return rowPage.map(e -> e.toEntity(resultClass));
    }

    @Override
    public Iterator<RowResult> iterator() {
        return rowPage.iterator();
    }

}
