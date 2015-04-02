package com.harmony.modules.utils;

import java.io.IOException;

import org.junit.Test;

import com.harmony.modules.io.Resource;
import com.harmony.modules.io.utils.ResourceScaner;

public class ResourceScanerTest {

    @Test
    public void test() throws IOException {
        Resource[] resources = ResourceScaner.scanPath("com.harmony");
        for (Resource res : resources) {
            System.out.println(res);
        }
    }

    @Test
    public void testClass() throws IOException {
        Class<?>[] classes = ResourceScaner.getInstance().scanPackage("");
        for (Class<?> clazz : classes) {
            System.out.println(clazz);
        }
    }

}
