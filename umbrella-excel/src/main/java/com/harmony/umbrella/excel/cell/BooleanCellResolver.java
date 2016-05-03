package com.harmony.umbrella.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class BooleanCellResolver extends AbstractCellResolver<Boolean> {

    public static final BooleanCellResolver INSTANCE = new BooleanCellResolver();

    @Override
    public Boolean resolve(int rowIndex, int columnIndex, Cell cell) {
        return ExcelUtil.getBooleanCellValue(cell);
    }

}
