package com.harmony.umbrella.context.ee;

import java.io.FileInputStream;
import java.util.Map;

import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationPropertiesTest {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
        String jsonText = IOUtils.toString(new FileInputStream("src/test/resources/application.json"));
        System.out.println(jsonText);
        Map map = Json.parse(jsonText, Map.class);
        System.out.println(map);
    }

}
