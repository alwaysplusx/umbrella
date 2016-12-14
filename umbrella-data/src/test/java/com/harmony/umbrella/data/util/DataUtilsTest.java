package com.harmony.umbrella.data.util;

import java.util.Calendar;

import com.harmony.umbrella.data.entity.Model;

/**
 * @author wuxii@foxmail.com
 */
public class DataUtilsTest {

    public static void main(String[] args) {
        Model model = new Model();
        model.setCode("wuxii");
        model.setCreateDate(Calendar.getInstance());
    }

}
