package com.harmony.umbrella.fs;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;

/**
 * @author wuxii@foxmail.com
 */
public class StorageManagerTest {

    public static void main(String[] args) throws IOException {
        System.out.println(new File("/umbrella-ss").getAbsolutePath());
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
    }

}
