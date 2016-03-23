/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.excel;

import java.util.Iterator;

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
    }

    @Override
    public void setRowNum(int rowNum) {
    }

    @Override
    public int getRowNum() {
        return 0;
    }

    @Override
    public Cell getCell(int cellnum) {
        return null;
    }

    @Override
    public Cell getCell(int cellnum, MissingCellPolicy policy) {
        return null;
    }

    @Override
    public short getFirstCellNum() {
        return 0;
    }

    @Override
    public short getLastCellNum() {
        return 0;
    }

    @Override
    public int getPhysicalNumberOfCells() {
        return 0;
    }

    @Override
    public void setHeight(short height) {
    }

    @Override
    public void setZeroHeight(boolean zHeight) {
    }

    @Override
    public boolean getZeroHeight() {
        return false;
    }

    @Override
    public void setHeightInPoints(float height) {
    }

    @Override
    public short getHeight() {
        return 0;
    }

    @Override
    public float getHeightInPoints() {
        return 0;
    }

    @Override
    public boolean isFormatted() {
        return false;
    }

    @Override
    public CellStyle getRowStyle() {
        return null;
    }

    @Override
    public void setRowStyle(CellStyle style) {
    }

    @Override
    public Iterator<Cell> cellIterator() {
        return null;
    }

    @Override
    public Sheet getSheet() {
        return null;
    }

    @Override
    public int getOutlineLevel() {
        return 0;
    }

}
