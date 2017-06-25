package com.harmony.umbrella.data.jpa;

import java.io.IOException;
import java.net.URL;

/**
 * @author wuxii@foxmail.com
 */
public class JarLoaderTest {

    public static void main(String[] args) throws IOException {
        URL url = new URL("jar:http://localhost:8080/a.jar!/");
        System.out.println(url);
        url.getContent();
    }

}
