package com.harmony.umbrella.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.harmony.umbrella.util.FileUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 *
 */
public class ExcelUtil {

    private static final String[] ALPHABETIC;

    // xls 256, xlsx 16384
    static {
        ALPHABETIC = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");
    }

    /**
     * excel(97-2003)扩展名
     */
    public static final String EXTENSION_XLS = ".xls";
    /**
     * excel2007扩展名
     */
    public static final String EXTENSION_XLSX = ".xlsx";

    /**
     * xls文件头
     */
    private static final String TYPE_XLS = "504b0304";

    /**
     * xlsx文件头
     */
    private static final String TYPE_XLSX = "d0cf11e0";

    public static Workbook getWorkBook(String path) throws IOException {
        return getWorkBook(new File(path));
    }

    public static Workbook getWorkBook(File file) throws IOException {
        if (!file.isFile()) {
            throw new IOException(file.getAbsolutePath() + " not a file");
        }
        String type = FileUtils.getExtension(file);
        if (StringUtils.isBlank(type)) {
            type = getType(file);
        }
        return getWorkBook(new FileInputStream(file), type);
    }

    public static Workbook getWorkBook(InputStream is, String type) throws IOException {
        if (EXTENSION_XLS.equals(type)) {
            return new HSSFWorkbook(is);
        } else if (EXTENSION_XLSX.equals(type)) {
            return new XSSFWorkbook(is);
        }
        throw new IOException("unrecognized excel file");
    }

    public static Sheet getSheet(File file, String sheetName) throws IOException {
        return getWorkBook(file).getSheet(sheetName);
    }

    public static Sheet getFirstSheet(File file) throws IOException {
        return getSheet(file, 0);
    }

    public static Sheet getSheet(File file, int sheetIndex) throws IOException {
        return getWorkBook(file).getSheetAt(sheetIndex);
    }

    public static Sheet[] getSheets(File file) throws IOException {
        Workbook wb = getWorkBook(file);
        int sheetCount = wb.getNumberOfSheets();
        Sheet[] sheets = new Sheet[sheetCount];
        for (int i = 0; i < sheetCount; i++) {
            sheets[i] = wb.getSheetAt(i);
        }
        return sheets;
    }

    public static void readSheet(Sheet sheet, RowVisitor visitor) {
        readSheet(sheet, 0, 1, visitor);
    }

    public static void readSheet(Sheet sheet, int header, int startRow, RowVisitor visitor) {
        SheetReader.create(sheet, header, startRow).read(visitor);
    }

    /**
     * 排除空行的最大行数. 文件行数从0开始
     *
     * @param sheet 工作表
     * @return 最大行数
     */
    public static int getMaxRowNumber(Sheet sheet) {
        return sheet.getLastRowNum();
    }

    /**
     * 表格指定行中的最大列, 列数从0开始计算
     *
     * @param sheet  工作表
     * @param rowNum 指定行
     * @return 最大列
     */
    public static int getMaxCellNumber(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            return 0;
        }
        return row.getLastCellNum();
    }

    public static Cell[] getCells(Sheet sheet, int rowNum) {
        List<Cell> cells = new ArrayList<Cell>();
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            for (Cell cell : row) {
                cells.add(cell);
            }
        }
        return cells.toArray(new Cell[cells.size()]);
    }

    /**
     * 按行列号定位cell
     *
     * @param sheet  工作表
     * @param rowNum    行号
     * @param columnNum 列号
     * @return cell
     */
    public static Cell getCell(Sheet sheet, int rowNum, int columnNum) {
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            return row.getCell(columnNum);
        }
        return null;
    }

    /**
     * 检测指定行列的cell是否是空cell(cell为null或者cell中的文本是empty)
     *
     * @param sheet     工作表
     * @param rowNum    行号
     * @param columnNum 列号
     * @return
     */
    public static boolean isEmptyCell(Sheet sheet, int rowNum, int columnNum) {
        Cell cell = getCell(sheet, rowNum, columnNum);
        return cell == null || StringUtils.isEmpty((cell.getStringCellValue()));
    }

    public static boolean hasRow(Sheet sheet, int rowNum) {
        return sheet.getRow(rowNum) != null;
    }

    public static boolean hasCell(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        return row != null && row.getFirstCellNum() != -1;
    }

    // cell value util

    @SuppressWarnings("rawtypes")
    public static <T extends Enum> T getEnumCellValue(Cell cell, Class<T> enumType) {
        for (T t : enumType.getEnumConstants()) {
            if (t.name().equals(cell.getStringCellValue())) {
                return t;
            }
        }
        return null;
    }

    public static Boolean getBooleanCellValue(Cell cell) {
        if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
            return cell.getBooleanCellValue();
        }
        return Boolean.valueOf(getStringCellValue(cell));
    }

    public static Number getNumberCellValue(Cell cell) {
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return cell.getNumericCellValue();
        } else if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
            return cell.getNumericCellValue();
        }
        return new BigDecimal(getStringCellValue(cell));
    }

    public static Date getDateCellValue(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        return null;
    }

    public static Date getDateCellValue(Cell cell, String pattern) throws ParseException {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        return new SimpleDateFormat(pattern).parse(getStringCellValue(cell));
    }

    public static String getStringCellValue(Cell cell) {
        Object result = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                result = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
                } else {
                    result = cell.getNumericCellValue();
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                result = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                // 公式型
                try {
                    // 获取计算后结果
                    result = cell.getNumericCellValue();
                } catch (IllegalStateException e) {
                    try {
                        result = cell.getRichStringCellValue();
                    } catch (IllegalStateException e1) {
                        result = cell.getCellFormula();
                    }
                }
                break;
            case Cell.CELL_TYPE_ERROR:
                result = cell.getErrorCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                result = "";
                break;
            default:
                break;
        }
        return result == null ? null : result.toString();
    }

    // judge method

    public static String toColumnName(int column) {
        String columnName = ALPHABETIC[column % 26 - 1];
        while (column / 26 > 0) {
            column = column / 26;
            columnName = ALPHABETIC[column % 26 - 1] + columnName;
        }
        return columnName;
    }

    public static String toCellName(Cell cell) {
        return toColumnName(cell.getColumnIndex() + 1) + (cell.getRowIndex() + 1);
    }

    public static boolean isXls(String pathname) {
        return isXls(new File(pathname));
    }

    public static boolean isXlsx(String pathname) {
        return isXlsx(new File(pathname));
    }

    public static boolean isXls(File file) {
        String extension = FileUtils.getExtension(file);
        if (StringUtils.isBlank(extension)) {
            try {
                return TYPE_XLS.equals(getType(file));
            } catch (IOException e) {
                return false;
            }
        }
        return EXTENSION_XLS.equals(extension);
    }

    public static boolean isXlsx(File file) {
        String extension = FileUtils.getExtension(file);
        if (StringUtils.isBlank(extension)) {
            try {
                return TYPE_XLSX.equals(getType(file));
            } catch (IOException e) {
                return false;
            }
        }
        return EXTENSION_XLSX.equals(extension);
    }

    /**
     * 读取文件的头, 首位的4个字节
     *
     * @throws IOException
     */
    private static String getType(File file) throws IOException {
        if (!file.isFile()) {
            throw new IOException(file.getAbsolutePath() + ", file is not a file");
        }
        StringBuilder sb = new StringBuilder();
        FileInputStream fis = new FileInputStream(file);
        int count = 0;
        while (count < 4) {
            sb.append(Integer.toHexString(fis.read()));
            count++;
        }
        fis.close();
        return sb.toString();
    }

}
