/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @author wuxii@foxmail.com
 */
public interface CellResolver<T> {

    /**
     * cell解析完的最终类型
     * 
     * @return
     */
    Class<T> targetType();

    /**
     * 解析cell
     * 
     * @param rowIndex
     *            cell的行
     * @param columnIndex
     *            cell的列号
     * @param cell
     *            cell
     * @return target type value
     */
    T resolve(int rowIndex, int columnIndex, Cell cell);

}
