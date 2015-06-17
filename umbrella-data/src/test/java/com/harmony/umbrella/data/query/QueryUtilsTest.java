/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.data.query;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.data.mapping.PropertyPath;

import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.persistence.User;

/**
 * @author wuxii@foxmail.com
 */
public class QueryUtilsTest {

    @Test
    public void test() {
        String applySorting = QueryUtils.applySorting("select x from User x", new Sort("name", "age"));
        System.out.println(applySorting);
    }

    @Test
    public void testExistQuery() {
        List<String> list = new ArrayList<String>();
        String string = QueryUtils.getExistsQueryString("User", "*", list);
        System.out.println(string);
    }

    @Test
    public void testCreateQueryFor() {
        String string = QueryUtils.createCountQueryFor("select * from User");
        System.out.println(string);
    }

    public static void main(String[] args) {
        PropertyPath property = PropertyPath.from("person.address", User.class);
        System.out.println(property);
    }

}
