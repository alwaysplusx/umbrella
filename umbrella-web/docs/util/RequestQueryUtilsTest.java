package com.harmony.umbrella.web.util;

import com.harmony.umbrella.web.util.RequestQueryUtils.FilterParameter;

/**
 * @author wuxii@foxmail.com
 */
public class RequestQueryUtilsTest {

    public static void main(String[] args) {
        FilterParameter fp1 = new FilterParameter("f_a_eq_name", "f", "_", User.class);
        FilterParameter fp2 = new FilterParameter("f_lk_user_age", "f", "_", User.class);
        System.out.println(RequestQueryUtils.toBond(fp1, fp2));

    }
}
