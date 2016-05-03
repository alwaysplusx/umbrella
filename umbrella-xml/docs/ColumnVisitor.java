package com.harmony.umbrella.excel;

/**
 * @author wuxii@foxmail.com
 */
public interface ColumnVisitor {

    void visitHeader(int header, ColumnWrapper column);

    boolean visitColumn(int y, ColumnWrapper column);

}
