package com.harmony.umbrella.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class StringCellResolver extends AbstractCellResolver<String> {

    public static final StringCellResolver INSTANCE = new StringCellResolver();

    @Override
    public String resolve(int rowIndex, int columnIndex, Cell cell) {
        return ExcelUtil.getStringCellValue(cell);
    }

}
