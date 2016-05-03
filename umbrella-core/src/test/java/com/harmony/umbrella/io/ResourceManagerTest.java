package com.harmony.umbrella.io;

import org.junit.Test;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceManagerTest {

    private static final Log log = Logs.getLog(ResourceManagerTest.class);

    @Test
    public void test() {
        new Thread() {
            @Override
            public void run() {
                Class<?>[] classes = ResourceManager.getInstance().getClasses("org.apache.log4j");

                for (Class<?> clazz : classes) {
                    log.info("{}", clazz.getName());
                }
            }
        }.start();

        Class<?>[] classes = ResourceManager.getInstance().getClasses("org.apache.log4j");

        for (Class<?> clazz : classes) {
            log.info("{}", clazz.getName());
        }
    }

}
