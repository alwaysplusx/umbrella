package com.harmony.umbrella.excel;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author wuxii@foxmail.com
 */
public class UserEntityTest {

    public static void main(String[] args) throws IOException {
        Sheet sheet = ExcelUtil.getFirstSheet(new File("src/test/resources/excel/user.xls"));
        SheetReader reader = new SheetReader(sheet, 0, 1);
        RowEntityMapper<User> emrv = RowEntityMapper.create(User.class, new String[] { "name", "age", "man" });
        reader.read(emrv);
        for (User u : emrv.getEntities()) {
            System.out.println(u);
        }
    }
}
