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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.CellResolver;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class CellResolverChain {

    protected static final List<CellResolver> RESOLVERS;

    static {
        List<CellResolver> resolvers = new ArrayList<CellResolver>();
        resolvers = new ArrayList<CellResolver>();
        resolvers.add(BooleanCellResolver.INSTANCE);
        resolvers.add(CalendarCellResolver.INSTANCE);
        resolvers.add(DateCellResolver.INSTANCE);
        resolvers.add(NumberCellResolver.INSTANCE);
        resolvers.add(StringCellResolver.INSTANCE);
        RESOLVERS = Collections.unmodifiableList(resolvers);
    }

    public static final CellResolverChain INSTANCE = new CellResolverChain(RESOLVERS);

    protected final List<CellResolver> resolvers;

    public CellResolverChain(List<CellResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @SuppressWarnings("unchecked")
    public <T> T doChain(Class<T> type, Cell cell) {
        for (CellResolver cr : RESOLVERS) {
            if (cr.isTargetType(type)) {
                return (T) cr.resolve(cell.getRowIndex(), cell.getColumnIndex(), cell);
            }
        }
        return (T) (Integer) 1;
        // throw new IllegalArgumentException("unresolver cell type " + cell.getCellType());
    }
}
