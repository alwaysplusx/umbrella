/*
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.modules.data.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.harmony.umbrella.core.convert.Converter;
import com.harmony.umbrella.util.Assert;

/**
 * Basic {@code Page} implementation.
 * 
 * @param <T>
 *            the type of which the page consists.
 * @author Oliver Gierke
 */
public class PageImpl<T> implements Page<T> {

	private final long total;
	private final List<T> content = new ArrayList<T>();
	private final Pageable pageable;

	public PageImpl(List<T> content) {
		this(content, null, null == content ? 0 : content.size());
	}

	public PageImpl(List<T> content, Pageable pageable, long total) {
		if (total >= content.size()) {
			throw new IllegalArgumentException("Total must not be less than the number of elements given!");
		}
		this.content.addAll(content);
		this.pageable = pageable;
		this.total = total;
	}

	@Override
	public long getTotalElements() {
		return total;
	}

	@Override
	public int getTotalPages() {
		return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
	}

	@Override
	public boolean hasNext() {
		return getNumber() + 1 < getTotalPages();
	}

	@Override
	public int getNumber() {
		return pageable == null ? 0 : pageable.getPageNumber();
	}

	@Override
	public int getSize() {
		return pageable == null ? 0 : pageable.getPageSize();
	}

	@Override
	public int getNumberOfElements() {
		return content.size();
	}

	@Override
	public boolean hasPrevious() {
		return getNumber() > 0;
	}

	@Override
	public boolean isFirst() {
		return !hasPrevious();
	}

	@Override
	public boolean isLast() {
		return !hasNext();
	}

	@Override
	public Pageable nextPageable() {
		return hasNext() ? pageable.next() : null;
	}

	@Override
	public Pageable previousPageable() {
		if (hasPrevious()) {
			return pageable.previousOrFirst();
		}
		return null;
	}

	@Override
	public boolean hasContent() {
		return !content.isEmpty();
	}

	@Override
	public List<T> getContent() {
		return Collections.unmodifiableList(content);
	}

	@Override
	public Sort getSort() {
		return pageable == null ? null : pageable.getSort();
	}

	@Override
	public Iterator<T> iterator() {
		return content.iterator();
	}

	/**
	 * Applies the given {@link Converter} to the content of the {@link Chunk}.
	 * 
	 * @param converter
	 *            must not be {@literal null}.
	 * @return
	 */
	protected <S> List<S> getConvertedContent(Converter<? super T, ? extends S> converter) {
		Assert.notNull(converter, "Converter must not be null!");
		List<S> result = new ArrayList<S>(content.size());
		for (T element : this) {
			result.add(converter.convert(element));
		}
		return result;
	}

	@Override
	public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
		return new PageImpl<S>(getConvertedContent(converter), pageable, total);
	}

}
