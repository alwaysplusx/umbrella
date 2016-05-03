package com.harmony.umbrella.excel.cell;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;

/**
 * @author wuxii@foxmail.com
 */
public class DateCellResolver extends AbstractCellResolver<Date> {

    public static final DateCellResolver INSTANCE = new DateCellResolver();

    private static final Set<String> PATTERNS = new HashSet<String>();

    static {
        PATTERNS.add("yyyy-MM-dd HH:mm:ss");
        PATTERNS.add("yyyy-MM-dd");
        PATTERNS.add("yyyy/MM/dd");
    }

    @Override
    public Date resolve(int rowIndex, int columnIndex, Cell cell) {
        Date date = ExcelUtil.getDateCellValue(cell);
        if (date == null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
            for (String pattern : PATTERNS) {
                try {
                    date = ExcelUtil.getDateCellValue(cell, pattern);
                } catch (ParseException e) {
                }
            }
        }
        return date;
    }

}
