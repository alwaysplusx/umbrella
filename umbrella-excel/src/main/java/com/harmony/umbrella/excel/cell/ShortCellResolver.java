package com.harmony.umbrella.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class ShortCellResolver extends AbstractCellResolver<Short> {

    public static final ShortCellResolver INSTANCE = new ShortCellResolver();

    @Override
    public Short resolve(int rowIndex, int columnIndex, Cell cell) {
        Number number = ExcelUtil.getNumberCellValue(cell);
        return number != null ? number.shortValue() : null;
    }

}
