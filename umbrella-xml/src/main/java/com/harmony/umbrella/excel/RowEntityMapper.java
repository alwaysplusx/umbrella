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
package com.harmony.umbrella.excel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.harmony.umbrella.core.AccessMember;
import com.harmony.umbrella.excel.annotation.ExcelColumn;
import com.harmony.umbrella.excel.cell.CellResolverChain;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.CollectionUtils;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class RowEntityMapper<T> implements RowVisitor {

    private Class<T> entityClass;

    private Map<Integer, AccessMember> converterMap;

    private Map<String, String> headerFieldMap;

    private CellResolverChain chain;

    private final List<T> result = new ArrayList<T>();

    private int startColumn = 0;

    private int endColumn = -1;

    public RowEntityMapper(Class<T> entityClass, Map<Integer, AccessMember> converterMap, CellResolverChain chain) {
        this.entityClass = entityClass;
        this.converterMap = converterMap;
        this.chain = chain;
    }

    public RowEntityMapper(Class<T> entityClass,
                           Map<String, String> headerFieldMap,
                           Map<Integer, AccessMember> converterMap,
                           CellResolverChain chain,
                           int startColumn,
                           int endColumn) {
        this.entityClass = entityClass;
        this.converterMap = converterMap;
        this.headerFieldMap = headerFieldMap;
        this.chain = chain;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    @Override
    public void visitHeader(int header, Row row) {
        if (headerFieldMap != null && !headerFieldMap.isEmpty()) {
            for (int i = startColumn, max = getMaxColumnNumber(row); i < max; i++) {
                if (!converterMap.containsKey(i)) {
                    Cell cell = row.getCell(i);
                    if (cell == null) {
                        throw new IllegalArgumentException(ExcelUtil.toCellName(cell) + " empty cell of header column");
                    }
                    converterMap.put(i, new AccessMember(entityClass, headerFieldMap.get(cell.getStringCellValue())));
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean visitRow(int rowNum, Row row) {
        T entity = newEntity();
        for (int i = startColumn, max = getMaxColumnNumber(row); i < max; i++) {
            AccessMember accessMember = converterMap.get(i);
            if (accessMember == null) {
                throw new IllegalStateException("unknow column " + i);
            }
            Cell cell = row.getCell(i);
            CellResolver cr = getCellResolver(accessMember);
            Object cellValue = null;
            if (cr != null) {
                cellValue = cr.resolve(cell.getRowIndex(), cell.getColumnIndex(), cell);
            } else {
                cellValue = chain.doChain(accessMember.getType(), cell);
            }
            accessMember.set(entity, cellValue);
        }
        // when entity all access member already set add to result
        result.add(entity);
        return true;
    }

    @SuppressWarnings("rawtypes")
    protected CellResolver getCellResolver(AccessMember accessMember) {
        Field field = accessMember.getField();
        ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
        if (ann != null && ClassFilterFeature.NEWABLE.accept(ann.cellResolver())) {
            return ReflectionUtils.instantiateClass(ann.cellResolver());
        }
        return null;
    }

    protected T newEntity() {
        return ReflectionUtils.instantiateClass(entityClass);
    }

    public Iterator<T> iteratorEntity() {
        return result.iterator();
    }

    public T[] getEntities() {
        return CollectionUtils.toArray(result, entityClass);
    }

    protected int getMaxColumnNumber(Row row) {
        if (endColumn == -1) {
            return row.getLastCellNum();
        }
        return endColumn;
    }

    // factory method

    /**
     * 通过字段与列的关系建立RowEntityMapper
     * <p>
     * <pre>
     * fields[0] = excel中的第1列
     * fields[1] = excel中的第2列
     * ...
     * fields[n] = excel中的第n+1列
     * </pre>
     *
     * @param entityClass 映射为的类
     * @param fields      映射的字段
     * @return
     */
    public static <T> RowEntityMapper<T> create(Class<T> entityClass, String[] fields) {
        Map<Integer, AccessMember> converterMap = new HashMap<Integer, AccessMember>();
        for (int i = 0; i < fields.length; i++) {
            converterMap.put(i, new AccessMember(entityClass, fields[i]));
        }
        return new RowEntityMapper<T>(entityClass, converterMap, CellResolverChain.INSTANCE);
    }

    /**
     * 通过字段的列与字段名称建立RowEntityMapper
     * <p>
     * <pre>
     * fieldMap.key = 0 = excel中的第1列
     * fieldMap.key = 1 = excel中的第2列
     * ...
     * fieldMap.key = n = excel中的第n+1列
     * </pre>
     *
     * @param entityClass 映射为的类
     * @param fieldMap    列好与映射的字段对应关系
     * @return
     */
    public static <T> RowEntityMapper<T> create(Class<T> entityClass, Map<Integer, String> fieldMap) {
        Map<Integer, AccessMember> converterMap = new HashMap<Integer, AccessMember>();
        for (Entry<Integer, String> entry : fieldMap.entrySet()) {
            converterMap.put(entry.getKey(), new AccessMember(entityClass, entry.getValue()));
        }
        return new RowEntityMapper<T>(entityClass, converterMap, CellResolverChain.INSTANCE);
    }

    /**
     * 通过表头与字段的关系来建立RowEntityMapper
     * <p>
     * 只有在解析完具体的表格后才能得出具体的列与字段的映射关系. 在表头名称不表的情况下,用户可以任意修改表列的顺序而不影响映射关系
     *
     * @param entityClass    映射为的类
     * @param headerFieldMap 表头与字段的映射关系
     * @return
     */
    public static <T> RowEntityMapper<T> createByHeaderFieldMapper(Class<T> entityClass, Map<String, String> headerFieldMap) {
        return new RowEntityMapper<T>(entityClass, headerFieldMap, null, CellResolverChain.INSTANCE, 0, -1);
    }

}
