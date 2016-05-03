package com.harmony.umbrella.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 将sheet分割为头与内容部分,分别读取
 *
 * @author wuxii@foxmail.com
 */
public class SheetReader {

    // 读取的表格
    private final Sheet sheet;
    // 表格指定的表头
    private int header = 0;
    // 开始读取的行
    private int startRow = 0;
    // 结束行, -1表示读取到最后
    private int endRow = -1;

    public SheetReader(Sheet sheet) {
        this(sheet, 0, 0, -1);
    }

    public SheetReader(Sheet sheet, int header, int startRow) {
        this(sheet, header, startRow, -1);
    }

    public SheetReader(Sheet sheet, int header, int startRow, int endRow) {
        this.sheet = sheet;
        this.header = header;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    public void read(RowVisitor visitor) {
        // header first
        visitor.visitHeader(header, readHeader());
        for (int rowNum = this.startRow, maxRow = getMaxRowNumber(); rowNum <= maxRow; rowNum++) {
            if (isHeaderRow(rowNum)) {
                continue;
            }
            visitor.visitRow(rowNum, sheet.getRow(rowNum));
        }
    }

    /**
     * 读取表头
     *
     * @return 表头的行
     */
    public Row readHeader() {
        return sheet.getRow(header);
    }

    /**
     * 读取表的内容,排除了表头
     *
     * @return 表的内容
     */
    public Row[] readContent() {
        int maxRow = getMaxRowNumber();
        List<Row> rows = new ArrayList<Row>(maxRow);
        for (int rowNum = this.startRow; rowNum <= maxRow; rowNum++) {
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
