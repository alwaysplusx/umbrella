package com.harmony.umbrella.excel.cell;

import java.util.Calendar;

import org.apache.poi.ss.usermodel.Cell;

import com.harmony.umbrella.excel.ExcelUtil;
import com.harmony.umbrella.util.TimeUtils;

/**
 * @author wuxii@foxmail.com
 */
public class CalendarCellResolver extends AbstractCellResolver<Calendar> {

    public static final CalendarCellResolver INSTANCE = new CalendarCellResolver();

    @Override
    public Calendar resolve(int rowIndex, int columnIndex, Cell cell) {
        return TimeUtils.toCalendar(ExcelUtil.getDateCellValue(cell));
    }

}
