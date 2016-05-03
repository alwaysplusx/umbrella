package com.harmony.umbrella.xml;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class FetchingRss {

    public static void main(String[] args) throws Exception {
        URL url = new URL("http://www.geekpark.net/rss");

        InputStream inStream = url.openStream();
        FileOutputStream fos = new FileOutputStream("target/geekpark.xml");
        IOUtils.copy(inStream, fos);
        fos.close();
    }
}
