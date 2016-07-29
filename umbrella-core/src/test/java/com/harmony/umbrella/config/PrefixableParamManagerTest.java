package com.harmony.umbrella.config;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class PrefixableParamManagerTest {

    public static void main(String[] args) {
        PrefixableParamManager paramManager = new PrefixableParamManager(System.getProperties());
        paramManager.setPrefix("sun");
        paramManager.setFetchWithoutPrefix(true);
        List<Param> params = paramManager.getStartWith("os");
        System.out.println(params);
    }

}
