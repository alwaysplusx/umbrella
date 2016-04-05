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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class ExcelUtilTest {

    public static final File clazz = new File("target/classes/com/harmony/umbrella/excel/CellResolver.class");
    public static final File xls = new File("src/test/resources/excel/a.xls");
    public static final File xlsx = new File("src/test/resources/excel/a.xlsx");

    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream(xls);
        int count = 0;
        while (count < 4) {
            System.out.print(Integer.toHexString(fis.read()));
            count++;
        }
        fis.close();
        /*System.out.println();
        buf = FileUtils.readByte(xlsx);
        for (int i = 0; i < 8; i++) {
            System.out.print(buf[i] + " ");
        }*/
    }

    @Test
    public void testGetSheets() throws IOException {
        Sheet[] sheets = ExcelUtil.getSheets(xls);
        for (Sheet sheet : sheets) {
            System.out.println(sheet.getSheetName());
        }
    }

    @Test
    public void testSheet() throws IOException {
        Sheet sheet = ExcelUtil.getSheet(xlsx, 0);
        Row row = sheet.getRow(1000);
        System.out.println(row);
        int maxCellNumber = ExcelUtil.getMaxCellNumber(sheet, 0);
        System.out.println(maxCellNumber);
        //        System.out.println(sheet.getPhysicalNumberOfRows());
        //        System.out.println(sheet.getSheetName());
        //        System.out.println(sheet.getLastRowNum());
    }

    @Test
    public void testSheetReader() throws IOException {
        Sheet sheet = ExcelUtil.getSheet(xls, 0);
        SheetReader sr = SheetReader.create(sheet, 0, 0);
        sr.read(new RowVisitor() {

            @Override
            public void visitHeader(int header, RowWrapper row) {
                for (Cell cell : row) {
                    System.out.print("  " + ExcelUtil.getCellStringValue(cell) + "   ");
                }
                System.out.println();
            }

            @Override
            public boolean visitRow(int rowNum, RowWrapper row) {
                for (Cell cell : row) {
                    System.out.print(" " + ExcelUtil.getCellStringValue(cell) + " ");
                }
                System.out.println();
                return true;
            }

        });
    }

}
