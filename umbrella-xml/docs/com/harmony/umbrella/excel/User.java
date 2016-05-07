package com.harmony.umbrella.excel;

import com.harmony.umbrella.excel.annotation.ExcelColumn;

/**
 * @author wuxii@foxmail.com
 */
public class User {

    @ExcelColumn(0)
    private String name;

    @ExcelColumn(1)
    private int age;

    @ExcelColumn(2)
    private boolean man;

    @Override
    public String toString() {
        return "{name:" + name + ", age:" + age + ", man:" + man + "}";
    }

}
