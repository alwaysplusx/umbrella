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

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author wuxii@foxmail.com
 */
public class SheetReader {

    private int header = 0;
    private Sheet sheet;
    private int startRow = 0;

    public SheetReader(Sheet sheet) {
        this.sheet = sheet;
    }

    public void read(RowVisitor visitor) {
        int maxRow = getMaxRow();

        this.readHeader(visitor);

        for (int row = this.startRow; row < maxRow; row++) {
            if (isHeaderRow(row)) {
                continue;
            }
            if (!visitor.visitRow(row, new RowWrapper(sheet.getRow(row)))) {
                break;
            }
        }
    }

    public boolean isHeaderRow(int row) {
        return row == header;
    }

    public void readHeader(RowVisitor visitor) {
        visitor.visitHeader(header, new RowWrapper(sheet.getRow(header)));
    }

    public int getMaxRow() {
        return sheet.getLastRowNum();
    }

    /*public static void main(String[] args) throws IOException {
        new SheetReader(ExcelUtil.getFirstSheet("src/test/resources/a.xlsx")).read(new RowVisitor() {

            @Override
            public void visitHeader(int header, RowWrapper row) {
                for (Cell cell : row) {
                    System.out.print("   " + ExcelUtil.getCellValue(cell) + "    ");
                }
                System.out.println();
            }

            @Override
            public boolean visitRow(int y, RowWrapper row) {
                for (Cell cell : row) {
                    System.out.print(" " + ExcelUtil.getCellValue(cell) + "  ");
                }
                System.out.println();
                return true;
            }
        });
    }*/
}
