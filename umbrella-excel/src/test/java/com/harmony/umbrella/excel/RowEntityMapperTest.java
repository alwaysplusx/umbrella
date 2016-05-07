package com.harmony.umbrella.excel;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author wuxii@foxmail.com
 */
public class RowEntityMapperTest {

    public static void main(String[] args) throws IOException {
        Sheet sheet = ExcelUtil.getFirstSheet(new File("src/test/resources/users.xlsx"));
        RowEntityMapper<User> mapper = RowEntityMapper.createByClass(User.class);
        mapper.parse(sheet);
        for (User u : mapper.getEntities()) {
            System.out.println(u);
        }
    }

}
