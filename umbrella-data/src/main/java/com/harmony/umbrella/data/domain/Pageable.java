package com.harmony.umbrella.data.domain;

public interface Pageable {

	/**
	 * Returns the page to be returned.<p>当前是第几页
	 * 
	 * @return the page to be returned.
	 */
	int getPageNumber();

	/**
	 * Returns the number of items to be returned. <p>单个页面所包含的数据大小
	 * 
	 * @return the number of items of that page
	 */
	int getPageSize();

	/**
	 * Returns the offset to be taken according to the underlying page and page
	 * size.<p>当前页面数据起始位，位于总数据量的第几位
	 * 
	 * @return the offset to be taken
	 */
	int getOffset();

	/**
	 * Returns the sorting parameters. <p>排序信息
	 * 
	 * @return
	 */
	Sort getSort();

	/**
	 * Returns the {@link Pageable} requesting the next {@link Page}. <p>下一个分页情况
	 * 
	 * @return
	 */
	Pageable next();

	/**
	 * Returns the previous {@link Pageable} or the first {@link Pageable} if
	 * the current one already is the first one. <p>上页或者第一页的分页情况
	 * 
	 * @return
	 */
	Pageable previousOrFirst();

	/**
	 * Returns the {@link Pageable} requesting the first page. <p>第一页 的分页情况
	 * 
	 * @return
	 */
	Pageable first();

	/**
	 * Returns whether there's a previous {@link Pageable} we can access from
	 * the current one. Will return {@literal false} in case the current
	 * {@link Pageable} already refers to the first page. <p>是否有上页
	 * 
	 * @return
	 */
	boolean hasPrevious();
}
