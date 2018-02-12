package com.harmony.umbrella.web.util;

import java.util.Iterator;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author wuxii@foxmail.com
 */
public abstract class WebUtils {

    public static <T> Page<T> frontendPage(Page<T> page) {
        return new PageImpl<>(page);
    }

    public static class PageImpl<T> implements Page<T> {

        private final Page<T> page;

        public PageImpl(Page<T> page) {
            this.page = page;
        }

        @JSONField(name = "page", ordinal = 1)
        @Override
        public int getNumber() {
            return page.getNumber();
        }

        // @JSONField(name = "itemCount", ordinal = 2)
        @JSONField(serialize = false)
        @Override
        public int getNumberOfElements() {
            return page.getNumberOfElements();
        }

        @JSONField(name = "totals", ordinal = 3)
        @Override
        public long getTotalElements() {
            return page.getTotalElements();
        }

        @JSONField(ordinal = 4, name = "items")
        @Override
        public List<T> getContent() {
            return page.getContent();
        }

        // public int getNextCursor() {
        // return nextPageable() != null ? nextPageable().getPageNumber() : -1;
        // }
        //
        // public int getPreviousCursor() {
        // return previousPageable() != null ? previousPageable().getPageNumber() : -1;
        // }

        @JSONField(serialize = false)
        @Override
        public int getSize() {
            return page.getSize();
        }

        @JSONField(serialize = false)
        @Override
        public Sort getSort() {
            return page.getSort();
        }

        @Override
        @JSONField(serialize = false)
        public int getTotalPages() {
            return page.getTotalPages();
        }

        @JSONField(serialize = false)
        @Override
        public boolean isFirst() {
            return page.isFirst();
        }

        @JSONField(serialize = false)
        @Override
        public boolean isLast() {
            return page.isLast();
        }

        @Override
        public boolean hasContent() {
            return page.hasContent();
        }

        @Override
        public boolean hasNext() {
            return page.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return page.hasPrevious();
        }

        @Override
        public Pageable nextPageable() {
            return page.nextPageable();
        }

        @Override
        public Pageable previousPageable() {
            return page.previousPageable();
        }

        @Override
        public Iterator<T> iterator() {
            return page.iterator();
        }

        @Override
        public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
            return page.map(converter);
        }

    }

}
