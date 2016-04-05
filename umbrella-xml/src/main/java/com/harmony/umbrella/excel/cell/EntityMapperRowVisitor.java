/*
 * Copyright 2012-2016 the original author or authors.
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
package com.harmony.umbrella.excel.cell;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.CellWrapper;
import com.harmony.umbrella.excel.RowVisitor;
import com.harmony.umbrella.excel.RowWrapper;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class EntityMapperRowVisitor<T> implements RowVisitor {

    private Class<T> entityClass;

    private Map<Integer, String> headerNames = new HashMap<Integer, String>();

    private Map<Integer, Method> fieldMethodMap = new HashMap<Integer, Method>();

    @Override
    public void visitHeader(int header, RowWrapper row) {
        /*for (Integer i : headerNames.keySet()) {
            CellWrapper cw = row.getCellWrapper(i);
        }

        for (Cell c : row) {
            int column = c.getColumnIndex();
            String string = headerNames.get(column);
        }*/
    }

    @Override
    public boolean visitRow(int rowNum, RowWrapper row) {
        T entity = newEntity();
        for (Cell cell : row) {
            CellWrapper cw = new CellWrapper(cell);

        }
        return false;
    }

    protected T newEntity() {
        return ReflectionUtils.instantiateClass(entityClass);
    }

    public List<T> getEntitys() {
        return null;
    }

}
