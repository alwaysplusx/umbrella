package com.harmony.umbrella.util;

import java.io.File;
import java.io.IOException;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceUtilsTest {

    public static void main(String[] args) throws IOException {
        File file = ResourceUtils.getFile("log4j.xml");
        System.out.println(file.getAbsolutePath() + ", " + file.exists());
    }

}
