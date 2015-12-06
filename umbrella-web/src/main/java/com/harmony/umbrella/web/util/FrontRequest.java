/*
 * Copyright 2012-2015 the original author or authors.
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
