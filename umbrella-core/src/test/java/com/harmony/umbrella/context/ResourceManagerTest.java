package com.harmony.umbrella.context;

import org.junit.Test;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourceManager;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceManagerTest {

    public static void main(String[] args) {

        Resource[] resources = ResourceManager.getInstance().getResources("com.harmony");
        for (Resource resource : resources) {
            System.out.println(resource.toString());
        }

        Class<?>[] classes = ResourceManager.getInstance().getClasses("com.harmony");
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }

    }

    @Test
    public void testGetResources() {
        Resource[] resources = ResourceManager.getInstance().getResources("com.harmony");
        for (Resource resource : resources) {
            System.out.println(resource.toString());
        }
    }

}
