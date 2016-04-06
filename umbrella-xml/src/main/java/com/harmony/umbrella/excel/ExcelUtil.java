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

    /**
     * 根据文件路径获取工作簿
     *
     * @param path 文件路径
     * @return 工作簿
     * @throws IOException 指定路径文件不存在
     */
    public static Workbook getWorkBook(String path) throws IOException {
        return getWorkBook(new File(path));
    }

    /**
     * 根据文件获取工作簿
     *
     * @param file 文件
     * @return 工作簿
     * @throws IOException 指定文件不存在
     */
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

    /**
     * 从输入流中读取错指定类型的工作簿
     *
     * @param is   输入流
     * @param type 文件类型 'xls'或'xlsx'
     * @return 工作簿
     * @throws IOException
     */
    public static Workbook getWorkBook(InputStream is, String type) throws IOException {
        if (EXTENSION_XLS.equals(type)) {
            return new HSSFWorkbook(is);
        } else if (EXTENSION_XLSX.equals(type)) {
            return new XSSFWorkbook(is);
        }
        throw new IOException("unrecognized excel file");
    }

    /**
     * 从文件中读取出指定工作表格名称的表格
     *
     * @param file      文件
     * @param sheetName 表格名称
     * @return 指定名称的表格, 如果指定名称的表格不存在返回null
     * @throws IOException 文件不存在
     */
    public static Sheet getSheet(File file, String sheetName) throws IOException {
        return getWorkBook(file).getSheet(sheetName);
    }

    /**
     * 读取文件中第一个表格
     *
     * @param file 文件
     * @return index为0的第一个表格
     * @throws IOException 文件不存在
     */
    public static Sheet getFirstSheet(File file) throws IOException {
        return getSheet(file, 0);
    }

    /**
     * 读取指定index的表格, index由0开始
     *
     * @param file       文件
     * @param sheetIndex 表格的index
     * @return 表格
     * @throws IOException              文件不存在
     * @throws IllegalArgumentException 指定的index超出最大的index
     */
    public static Sheet getSheet(File file, int sheetIndex) throws IOException {
        return getWorkBook(file).getSheetAt(sheetIndex);
    }

    /**
     * 读取出文件中所有的工作表格
     *
     * @param file excel文件
     * @return 工作簿中的所有表格
     * @throws IOException 文件不存在
     */
    public static Sheet[] getSheets(File file) throws IOException {
        Workbook wb = getWorkBook(file);
        int sheetCount = wb.getNumberOfSheets();
        Sheet[] sheets = new Sheet[sheetCount];
        for (int i = 0; i < sheetCount; i++) {
            sheets[i] = wb.getSheetAt(i);
        }
        return sheets;
    }

    /**
     * 表格的第一行做表头,从第2行内容开始读取.使用visitor读取表格
     *
     * @param sheet   表格
     * @param visitor 按行读取工具
     */
    public static void readSheet(Sheet sheet, RowVisitor visitor) {
        readSheet(sheet, 0, 1, visitor);
    }

    /**
     * 从指定的行读取sheet, 支持自定义行头以及开始行
     *
     * @param sheet    表格
     * @param header   表头的行
     * @param startRow 开始读取的行
     * @param visitor  按行读取工具
     */
    public static void readSheet(Sheet sheet, int header, int startRow, RowVisitor visitor) {
        new SheetReader(sheet, header, startRow).read(visitor);
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

    /**
     * 按行列号定位cell
     *
     * @param sheet     工作表
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

    /**
     * 将cell对应文本值转化为输入的枚举对象, 采用文本匹配方式(忽略大小写)
     *
     * @param cell     单元格
     * @param enumType 枚举类型
     * @param <T>      枚举
     * @return 与cell文本匹配的枚举类
     */
    @SuppressWarnings("rawtypes")
    public static <T extends Enum> T getEnumCellValue(Cell cell, Class<T> enumType) {
        for (T t : enumType.getEnumConstants()) {
            if (t.name().equalsIgnoreCase(cell.getStringCellValue())) {
                return t;
            }
        }
        return null;
    }

    /**
     * 获取cell的boolean值, 如果不是{@linkplain Cell#CELL_TYPE_BOOLEAN}
     * 则通过Boolean.valueOf()判定boolean值
     *
     * @param cell 单元格
     * @return boolean
     */
    public static Boolean getBooleanCellValue(Cell cell) {
        if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
            return cell.getBooleanCellValue();
        }
        return Boolean.valueOf(getStringCellValue(cell));
    }

    /**
     * 获取cell的数值
     *
     * @param cell 单元格
     * @return number
     */
    public static Number getNumberCellValue(Cell cell) {
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return cell.getNumericCellValue();
        } else if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
            return cell.getNumericCellValue();
        }
        return new BigDecimal(getStringCellValue(cell));
    }

    /**
     * 如果cell是{@linkplain Cell#CELL_TYPE_NUMERIC} &&
     * {@linkplain DateUtil#isCellDateFormatted(Cell)}成立则返回单元格对应的时间
     *
     * @param cell 单元格
     * @return date
     */
    public static Date getDateCellValue(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        return null;
    }

    /**
     * 如果cell是{@linkplain Cell#CELL_TYPE_NUMERIC} &&
     * {@linkplain DateUtil#isCellDateFormatted(Cell)}成立则返回单元格对应的时间.
     * 如果对应的时间是文本格式通过{@linkplain SimpleDateFormat}转化后返回对应的时间
     *
     * @param cell    单元格
     * @param pattern 时间格式化的模版
     * @return date
     */
    public static Date getDateCellValue(Cell cell, String pattern) throws ParseException {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        return new SimpleDateFormat(pattern).parse(getStringCellValue(cell));
    }

    /**
     * 将cell的值转为文本值返回
     *
     * @param cell 单元格
     * @return string
     */
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

    private static String toColumnName(int column) {
        String columnName = ALPHABETIC[column % 26 - 1];
        while (column / 26 > 0) {
            column = column / 26;
            columnName = ALPHABETIC[column % 26 - 1] + columnName;
        }
        return columnName;
    }

    /**
     * 返回单元格对应的坐标名称, 如: (0, 0) -> A1
     *
     * @param cell 单元格
     * @return 单元格名称
     */
    public static String toCellName(Cell cell) {
        return toColumnName(cell.getColumnIndex() + 1) + (cell.getRowIndex() + 1);
    }

    /**
     * @see #isXls(File)
     */
    public static boolean isXls(String pathname) {
        return isXls(new File(pathname));
    }

    /**
     * @see #isXlsx(File)
     */
    public static boolean isXlsx(String pathname) {
        return isXlsx(new File(pathname));
    }

    /**
     * 通过扩展名判断是否是excel xls类型文件, 如果无法通过文件扩展名判断则通过读取文件首的4个字符判断文件类型
     *
     * @param file 文件路径
     * @return
     */
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

    /**
     * 通过扩展名判断是否是excel xlsx类型文件, 如果无法通过文件扩展名判断则通过读取文件首的4个字符判断文件类型
     *
     * @param file 文件路径
     * @return
     */
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
