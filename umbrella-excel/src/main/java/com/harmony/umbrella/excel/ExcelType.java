package com.harmony.umbrella.excel;

/**
 * 
 * @author wuxii@foxmail.com
 */
public enum ExcelType {

    XLS(".xls"), XLSX(".xlsx");

    private final String extension;

    private ExcelType(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }

    public static ExcelType fromExtension(String extension) {
        for (ExcelType t : values()) {
            if (t.extension.equals(extension)) {
                return t;
            }
        }
        return null;
    }

}