package com.harmony.umbrella.data.sql;

/**
 * @author wuxii@foxmail.com
 */
public class SQLFormatTest {

    public static void main(String[] args) {
        String sql = "select o from Student o where (o.studentId in :studentId or o.age in :age) and o.studentName = :studentName";
        System.out.println(SQLFormat.format(sql));
    }
}
