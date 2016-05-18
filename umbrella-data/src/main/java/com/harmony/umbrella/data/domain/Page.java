package com.harmony.umbrella.data.domain;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.harmony.umbrella.util.Converter;

public interface Page<T> extends Iterable<T> {

    /**
     * 当前第几页
     * 
     * @return
     */
    int getNumber();

    /**
     * 页面数据内容限额数量
     * 
     * @return
     */
    int getSize();

    /**
     * 当前页有几条数据
     * 
     * @return
     */
    int getNumberOfElements();

    /**
     * 数据总数
     * 
     * @return
     */
    long getTotalElements();

    /**
     * Returns the number of total pages.
     * <p>
     * 总页数
     * 
     * @return the number of toral pages
     */
    int getTotalPages();

    /**
     * Returns the page content as {@link List}.
     * <p>
     * 页面内容
     * 
     * @return
     */
    List<T> getContent();

    /**
     * Returns the sorting parameters for the.
     * <p>
     * 排序信息
     * 
     * @return
     */
    Sort getSort();

    /**
     * Returns whether the has content at all.
     * <p>
     * 页面是否包含内容
     * 
     * @return
     */
    boolean hasContent();

    /**
     * Returns whether the current is the first one.
     * <p>
     * 是否是第一页
     * 
     * @return
     */
    boolean isFirst();

    /**
     * Returns whether the current is the last one.
     * <p>
     * 是否为最后一页
     * 
     * @return
     */
    boolean isLast();

    /**
     * Returns if there is a next.
     * <p>
     * 是否有下一页
     * 
     * @return if there is a next.
     */
    boolean hasNext();

    /**
     * Returns if there is a previous.
     * <p>
     * 是否有上一页
     * 
     * @return if there is a previous.
     */
    boolean hasPrevious();

    /**
     * Returns the {@link Pageable} to request the next {@link Slice}. Can be
     * {@literal null} in case the current {@link Slice} is already the last
     * one. Clients should check {@link #hasNext()} before calling this method
     * to make sure they receive a non-{@literal null} value.
     * 
     * @return
     */
    Pageable nextPageable();

    /**
     * Returns the {@link Pageable} to request the previous {@link Slice}. Can
     * be {@literal null} in case the current {@link Slice} is already the first
     * one. Clients should check {@link #hasPrevious()} before calling this
     * method make sure receive a non-{@literal null} value.
     * 
     * @return
     */
    Pageable previousPageable();

    /**
     * Returns a new {@link Page} with the content of the current one mapped by
     * the given {@link Converter}.
     *
     * @param converter
     *            must not be {@literal null}.
     * @return a new {@link Page} with the content of the current one mapped by
     *         the given {@link Converter}.
     * @since 1.10
     */
    <S> Page<S> map(Converter<? super T, ? extends S> converter);

}
