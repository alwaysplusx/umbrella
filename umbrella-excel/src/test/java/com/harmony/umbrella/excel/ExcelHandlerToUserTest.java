package com.harmony.umbrella.excel;

/**
 * @author wuxii@foxmail.com
 */
public class ExcelHandlerToUserTest {

    public static void main(String[] args) throws Exception {
        SheetSaxReader reader = new SheetSaxReader("src/test/resources/users.xlsx");
        reader.read(new UserHandler(), 0);
    }

    public static class UserHandler extends ExcelHandler {

        @Override
        public void handleRow(R r) {
            for (C c : r.getCells()) {
                System.out.println(ExcelUtil.toColumnName(c.columnNum + 1) + (c.rowNum + 1) + " = " + c.getStringValue());
            }
        }

    }
}
