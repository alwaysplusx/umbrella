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

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author wuxii@foxmail.com
 */
public class ExcelToUserTest {

    public static void main(String[] args) throws IOException {
        Sheet sheet = ExcelUtil.getFirstSheet(new File("src/test/resources/users.xlsx"));
        User[] users = RowEntityMapper.createByClass(User.class).parse(sheet);
        for (User u : users) {
            System.out.println(u);
        }
    }

}
