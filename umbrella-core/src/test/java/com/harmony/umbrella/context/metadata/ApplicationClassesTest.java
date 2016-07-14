package com.harmony.umbrella.context.metadata;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationClassesTest {

    public static void main(String[] args) {
        ApplicationClasses.addApplicationPackage("com.harmony.aws", "com.harmony.asm", "com.harmony.umbrella.core");
        Class<?>[] classes = ApplicationClasses.getAllClasses();
        for (Class<?> c : classes) {
            System.out.println(c.getName());
        }
    }
}
