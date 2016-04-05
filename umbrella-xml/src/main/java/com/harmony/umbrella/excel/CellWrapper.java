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

import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author wuxii@foxmail.com
 */
public class CellWrapper implements Cell {

    private final Cell cell;

    public CellWrapper(Cell cell) {
        this.cell = cell;
    }

    @Override
    public int getColumnIndex() {
        return cell.getColumnIndex();
    }

    @Override
    public int getRowIndex() {
        return cell.getRowIndex();
    }

    @Override
    public Sheet getSheet() {
        return cell.getSheet();
    }

    @Override
    public Row getRow() {
        return cell.getRow();
    }

    @Override
    public void setCellType(int cellType) {
        cell.setCellType(cellType);
    }

    @Override
    public int getCellType() {
        return cell.getCellType();
    }

    @Override
    public int getCachedFormulaResultType() {
        return cell.getCachedFormulaResultType();
    }

    @Override
    public void setCellValue(double value) {
        cell.setCellValue(value);
    }

    @Override
    public void setCellValue(Date value) {
        cell.setCellValue(value);
    }

    @Override
    public void setCellValue(Calendar value) {
        cell.setCellValue(value);
    }

    @Override
    public void setCellValue(RichTextString value) {
        cell.setCellValue(value);
    }

    @Override
    public void setCellValue(String value) {
        cell.setCellValue(value);
    }

    @Override
    public void setCellFormula(String formula) throws FormulaParseException {
        cell.setCellFormula(formula);
    }

    @Override
    public String getCellFormula() {
        return cell.getCellFormula();
    }

    @Override
    public double getNumericCellValue() {
        return cell.getNumericCellValue();
    }

    @Override
    public Date getDateCellValue() {
        return cell.getDateCellValue();
    }

    @Override
    public RichTextString getRichStringCellValue() {
        return cell.getRichStringCellValue();
    }

    @Override
    public String getStringCellValue() {
        return cell.getStringCellValue();
    }

    @Override
    public void setCellValue(boolean value) {
        cell.setCellValue(value);
    }

    @Override
    public void setCellErrorValue(byte value) {
        cell.setCellErrorValue(value);
    }

    @Override
    public boolean getBooleanCellValue() {
        return cell.getBooleanCellValue();
    }

    @Override
    public byte getErrorCellValue() {
        return cell.getErrorCellValue();
    }

    @Override
    public void setCellStyle(CellStyle style) {
        cell.setCellStyle(style);
    }

    @Override
    public CellStyle getCellStyle() {
        return cell.getCellStyle();
    }

    @Override
    public void setAsActiveCell() {
        cell.setAsActiveCell();
    }

    @Override
    public CellAddress getAddress() {
        return cell.getAddress();
    }

    @Override
    public void setCellComment(Comment comment) {
        cell.setCellComment(comment);
    }

    @Override
    public Comment getCellComment() {
        return cell.getCellComment();
    }

    @Override
    public void removeCellComment() {
        cell.removeCellComment();
    }

    @Override
    public Hyperlink getHyperlink() {
        return cell.getHyperlink();
    }

    @Override
    public void setHyperlink(Hyperlink link) {
        cell.setHyperlink(link);
    }

    @Override
    public void removeHyperlink() {
        cell.removeHyperlink();
    }

    @Override
    public CellRangeAddress getArrayFormulaRange() {
        return cell.getArrayFormulaRange();
    }

    @Override
    public boolean isPartOfArrayFormulaGroup() {
        return cell.isPartOfArrayFormulaGroup();
    }

}
