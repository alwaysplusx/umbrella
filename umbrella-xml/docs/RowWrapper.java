package com.harmony.umbrella.excel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author wuxii@foxmail.com
 */
public class RowWrapper implements Row {

    private Row row;

    public RowWrapper(Row row) {
        this.row = row;
    }

    public List<CellWrapper> getCellWrappers() {
        List<CellWrapper> cws = new ArrayList<CellWrapper>();
        for (Cell c : row) {
            cws.add(new CellWrapper(c));
        }
        return cws;
    }

    @Override
    public Iterator<Cell> iterator() {
        return row.iterator();
    }

    @Override
    public Cell createCell(int column) {
        return row.createCell(column);
    }

    @Override
    public Cell createCell(int column, int type) {
        return row.createCell(column, type);
    }

    @Override
    public void removeCell(Cell cell) {
        row.removeCell(cell);
    }

    @Override
    public void setRowNum(int rowNum) {
        row.setRowNum(rowNum);
    }

    @Override
    public int getRowNum() {
        return row.getRowNum();
    }

    public CellWrapper getCellWrapper(int cellnum) {
        return new CellWrapper(getCell(cellnum));
    }

    public CellWrapper getCellWrapper(int cellnum, MissingCellPolicy policy) {
        return new CellWrapper(getCell(cellnum, policy));
    }

    @Override
    public Cell getCell(int cellnum) {
        return row.getCell(cellnum);
    }

    @Override
    public Cell getCell(int cellnum, MissingCellPolicy policy) {
        return row.getCell(cellnum, policy);
    }

    @Override
    public short getFirstCellNum() {
        return row.getFirstCellNum();
    }

    @Override
    public short getLastCellNum() {
        return row.getLastCellNum();
    }

    @Override
    public int getPhysicalNumberOfCells() {
        return row.getPhysicalNumberOfCells();
    }

    @Override
    public void setHeight(short height) {
        row.setHeight(height);
    }

    @Override
    public void setZeroHeight(boolean zHeight) {
        row.setZeroHeight(zHeight);
    }

    @Override
    public boolean getZeroHeight() {
        return row.getZeroHeight();
    }

    @Override
    public void setHeightInPoints(float height) {
        row.setHeightInPoints(height);
    }

    @Override
    public short getHeight() {
        return row.getHeight();
    }

    @Override
    public float getHeightInPoints() {
        return row.getHeightInPoints();
    }

    @Override
    public boolean isFormatted() {
        return row.isFormatted();
    }

    @Override
    public CellStyle getRowStyle() {
        return row.getRowStyle();
    }

    @Override
    public void setRowStyle(CellStyle style) {
        row.setRowStyle(style);
    }

    @Override
    public Iterator<Cell> cellIterator() {
        return row.cellIterator();
    }

    @Override
    public Sheet getSheet() {
        return row.getSheet();
    }

    @Override
    public int getOutlineLevel() {
        return row.getOutlineLevel();
    }

}
