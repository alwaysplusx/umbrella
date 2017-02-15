package com.harmony.umbrella.web;

import java.util.StringTokenizer;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundleTest {

    public static void main(String[] args) {
        String text = "EQ_username_OR_LIKE_password_AND_EQ_age_OR_GT_class";
        StringTokenizer st = new StringTokenizer(text, "AND");
        for (; st.hasMoreTokens();) {
            System.out.println(st.nextToken());
        }
    }

}
