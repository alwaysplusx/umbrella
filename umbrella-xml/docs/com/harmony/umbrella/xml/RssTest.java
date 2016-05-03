package com.harmony.umbrella.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author wuxii@foxmail.com
 */
public class RssTest {

    public static void main(String[] args) throws Exception {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = parserFactory.newSAXParser();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("geekpark.xml");

        saxParser.parse(inputStream, new RssHandler());
    }
}
