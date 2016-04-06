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

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author wuxii@foxmail.com
 */
public class SheetReader {

    private final Sheet sheet;

    private int header = 0;

    private int startRow = 0;
    private int endRow = -1;

    public SheetReader(Sheet sheet, int header, int startRow, int endRow) {
        this.sheet = sheet;
        this.header = header;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    public static SheetReader create(Sheet sheet) {
        return create(sheet, 0, 1);
    }

    public static SheetReader create(Sheet sheet, int header, int startRow) {
        return create(sheet, header, startRow, -1);
    }

    public static SheetReader create(Sheet sheet, int header, int startRow, int endRow) {
        return new SheetReader(sheet, header, startRow, endRow);
    }

    public void read(RowVisitor visitor) {
        // header first
        visitor.visitHeader(header, readHeader());
        for (int rowNum = this.startRow, maxRow = getMaxRowNumber(); rowNum < maxRow; rowNum++) {
            if (isHeaderRow(rowNum)) {
                continue;
            }
            visitor.visitRow(rowNum, sheet.getRow(rowNum));
        }
    }

    public Row readHeader() {
        return sheet.getRow(header);
    }

    public Row[] readContent() {
        int maxRow = getMaxRowNumber();
        List<Row> rows = new ArrayList<Row>(maxRow);
        for (int rowNum = this.startRow; rowNum < maxRow; rowNum++) {
            if (isHeaderRow(rowNum)) {
                continue;
            }
            rows.add(sheet.getRow(rowNum));
        }
        return rows.toArray(new Row[rows.size()]);
    }

    public boolean isHeaderRow(int row) {
        return row == header;
    }

    public int getMaxRowNumber() {
        if (endRow == -1) {
            return ExcelUtil.getMaxRowNumber(sheet);
        }
        return endRow;
    }

}
