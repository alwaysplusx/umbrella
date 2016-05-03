package com.harmony.umbrella.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class FloatCellResolver extends AbstractCellResolver<Float> {

    public static final FloatCellResolver INSTANCE = new FloatCellResolver();

    @Override
    public Float resolve(int rowIndex, int columnIndex, Cell cell) {
        Number number = ExcelUtil.getNumberCellValue(cell);
        return number != null ? number.floatValue() : null;
    }

}
