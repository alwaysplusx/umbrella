package com.harmony.umbrella.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class DoubleCellResolver extends AbstractCellResolver<Double> {

    public static final DoubleCellResolver INSTANCE = new DoubleCellResolver();

    @Override
    public Double resolve(int rowIndex, int columnIndex, Cell cell) {
        Number number = ExcelUtil.getNumberCellValue(cell);
        return number != null ? number.doubleValue() : null;
    }

}
