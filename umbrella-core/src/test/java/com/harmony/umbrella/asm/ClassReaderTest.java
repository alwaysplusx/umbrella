package com.harmony.umbrella.asm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.util.ResourceUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ClassReaderTest {

    @Test
    public void test() throws IOException {
        final String className = ClassReaderTest.class.getPackage().getName().replace(".", "/") + "/" + ClassReaderTest.class.getSimpleName();
        String path = className + ".class";
        Resource res = ResourceUtils.findResource(path);
        InputStream is = res.getInputStream();
        ClassReader cr = new ClassReader(is);
        is.close();
        assertEquals(className, cr.getClassName());
    }
}
