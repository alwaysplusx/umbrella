package com.harmony.umbrella.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class ByteCellResolver extends AbstractCellResolver<Byte> {

    public static final ByteCellResolver INSTANCE = new ByteCellResolver();

    @Override
    public Byte resolve(int rowIndex, int columnIndex, Cell cell) {
        Number number = ExcelUtil.getNumberCellValue(cell);
        return number != null ? number.byteValue() : null;
    }

}
