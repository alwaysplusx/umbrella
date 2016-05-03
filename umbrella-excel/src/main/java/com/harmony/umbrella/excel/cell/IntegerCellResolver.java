package com.harmony.umbrella.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class IntegerCellResolver extends AbstractCellResolver<Integer> {

    public static final IntegerCellResolver INSTANCE = new IntegerCellResolver();

    @Override
    public Integer resolve(int rowIndex, int columnIndex, Cell cell) {
        Number number = ExcelUtil.getNumberCellValue(cell);
        return number != null ? number.intValue() : null;
    }

}
