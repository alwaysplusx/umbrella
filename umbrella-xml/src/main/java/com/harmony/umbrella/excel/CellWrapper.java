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

    @Override
    public int getColumnIndex() {
        return 0;
    }

    @Override
    public int getRowIndex() {
        return 0;
    }

    @Override
    public Sheet getSheet() {
        return null;
    }

    @Override
    public Row getRow() {
        return null;
    }

    @Override
    public void setCellType(int cellType) {
    }

    @Override
    public int getCellType() {
        return 0;
    }

    @Override
    public int getCachedFormulaResultType() {
        return 0;
    }

    @Override
    public void setCellValue(double value) {
    }

    @Override
    public void setCellValue(Date value) {
    }

    @Override
    public void setCellValue(Calendar value) {
    }

    @Override
    public void setCellValue(RichTextString value) {
    }

    @Override
    public void setCellValue(String value) {
    }

    @Override
    public void setCellFormula(String formula) throws FormulaParseException {
    }

    @Override
    public String getCellFormula() {
        return null;
    }

    @Override
    public double getNumericCellValue() {
        return 0;
    }

    @Override
    public Date getDateCellValue() {
        return null;
    }

    @Override
    public RichTextString getRichStringCellValue() {
        return null;
    }

    @Override
    public String getStringCellValue() {
        return null;
    }

    @Override
    public void setCellValue(boolean value) {
    }

    @Override
    public void setCellErrorValue(byte value) {
    }

    @Override
    public boolean getBooleanCellValue() {
        return false;
    }

    @Override
    public byte getErrorCellValue() {
        return 0;
    }

    @Override
    public void setCellStyle(CellStyle style) {
    }

    @Override
    public CellStyle getCellStyle() {
        return null;
    }

    @Override
    public void setAsActiveCell() {
    }

    @Override
    public CellAddress getAddress() {
        return null;
    }

    @Override
    public void setCellComment(Comment comment) {
    }

    @Override
    public Comment getCellComment() {
        return null;
    }

    @Override
    public void removeCellComment() {
    }

    @Override
    public Hyperlink getHyperlink() {
        return null;
    }

    @Override
    public void setHyperlink(Hyperlink link) {
    }

    @Override
    public void removeHyperlink() {
    }

    @Override
    public CellRangeAddress getArrayFormulaRange() {
        return null;
    }

    @Override
    public boolean isPartOfArrayFormulaGroup() {
        return false;
    }

}
