package com.harmony.umbrella.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class LongCellResolver extends AbstractCellResolver<Long> {

    public static final LongCellResolver INSTANCE = new LongCellResolver();

    @Override
    public Long resolve(int rowIndex, int columnIndex, Cell cell) {
        Number number = ExcelUtil.getNumberCellValue(cell);
        return number != null ? number.longValue() : null;
    }

}
