package com.harmony.umbrella.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @author wuxii@foxmail.com
 */
public interface CellResolver<T> {

    /**
     * cell解析完的最终类型
     * 
     * @return
     */
    Class<T> targetType();

    /**
     * 解析cell
     * 
     * @param rowIndex
     *            cell的行
     * @param columnIndex
     *            cell的列号
     * @param cell
     *            cell
     * @return target type value
     */
    T resolve(int rowIndex, int columnIndex, Cell cell);

}
