package com.harmony.umbrella.excel;

import org.apache.poi.ss.usermodel.Row;

public interface RowVisitor {

    void visitHeader(int header, Row row);

    boolean visitRow(int rowNum, Row row);

}
