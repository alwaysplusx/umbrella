package com.harmony.umbrella.core;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class PrefixablePropertyManagerTest {

    public static void main(String[] args) {
        PrefixablePropertyManager paramManager = new PrefixablePropertyManager(System.getProperties());
        paramManager.setPrefix("sun");
        paramManager.setFetchWithoutPrefix(true);
        List<Property> params = paramManager.getStartWith("os");
        System.out.println(params);
    }

}
