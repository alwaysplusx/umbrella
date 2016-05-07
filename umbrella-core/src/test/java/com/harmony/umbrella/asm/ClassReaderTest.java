package com.harmony.umbrella.asm;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.resource.FileSystemResource;

/**
 * @author wuxii@foxmail.com
 */
public class ClassReaderTest {

    @Test
    public void test() throws IOException {
        Resource resource = new FileSystemResource("target/classes/com/harmony/umbrella/core/BeanFactory.class");
        InputStream is = resource.getInputStream();

        ClassReader reader = new ClassReader(is);
        String className = reader.getClassName();

        System.out.println(className);
    }
}
