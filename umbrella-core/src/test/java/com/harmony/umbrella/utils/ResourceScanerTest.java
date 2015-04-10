package com.harmony.umbrella.utils;

import java.io.IOException;

import org.junit.Test;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.utils.ResourceScaner;

public class ResourceScanerTest {

    @Test
    public void test() throws IOException {
        Resource[] resources = ResourceScaner.getInstance().scanPath("com.harmony");
        for (Resource res : resources) {
            System.out.println(res);
        }
    }

    @Test
    public void testClass() throws IOException {
        Class<?>[] classes = ResourceScaner.getInstance().scanPackage("com.harmony");
        for (Class<?> clazz : classes) {
            System.out.println(clazz);
        }
    }

}
