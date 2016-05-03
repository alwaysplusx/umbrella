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
