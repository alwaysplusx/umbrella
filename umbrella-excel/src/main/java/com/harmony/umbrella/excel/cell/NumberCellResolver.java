package com.harmony.umbrella.excel.cell;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class NumberCellResolver extends AbstractCellResolver<Number> {

    public static final NumberCellResolver INSTANCE = new NumberCellResolver();

    @Override
    public Number resolve(int rowIndex, int columnIndex, Cell cell) {
        return new BigDecimal(ExcelUtil.getStringCellValue(cell));
    }

}
