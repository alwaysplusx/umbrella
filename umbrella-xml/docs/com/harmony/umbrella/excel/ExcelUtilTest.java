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
        SheetReader sr = new SheetReader(sheet, 0, 0);
        sr.read(new RowVisitor() {

            @Override
            public void visitHeader(int header, Row row) {
                for (Cell cell : row) {
                    System.out.print("  " + ExcelUtil.getStringCellValue(cell) + "   ");
                }
                System.out.println();
            }

            @Override
            public boolean visitRow(int rowNum, Row row) {
                for (Cell cell : row) {
                    System.out.print(" " + ExcelUtil.getStringCellValue(cell) + " ");
                }
                System.out.println();
                return true;
            }

        });
    }

    @Test
    public void testGetCellValue() throws IOException {
        Sheet sheet = ExcelUtil.getSheet(xls, "");
        Row row = sheet.getRow(0);
        for (Cell cell : row) {
            System.out.println(ExcelUtil.toCellName(cell) + ", " + ExcelUtil.getStringCellValue(cell));
        }
    }

}
