package com.harmony.umbrella.xml;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author wuxii@foxmail.com
 */
public class XmlTest {

    public static void main(String[] args) throws Exception {
        XmlMapper mapper = new XmlMapper();
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "b");
        mapper.writeValue(System.out, map);
    }
}
