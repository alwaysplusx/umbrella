package com.harmony.umbrella;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.util.IOUtils;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserTest {

    @Test
    public void testDepts() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        DefaultResourceLoader loader = new DefaultResourceLoader(cl);
        Resource resource = loader.getResource("classpath:/data.json");
        try (InputStream is = resource.getInputStream()) {
            String text = IOUtils.toString(is, "utf-8");
            JSONObject jsonObject = (JSONObject) Json.parse(text);
            ((JSONArray) jsonObject.get("data"))
                    .stream()
                    .map(e -> (JSONObject) e)
                    .filter(e -> Objects.nonNull(e.get("leave_date")) || Integer.valueOf(2).equals(e.get("status")))
                    .forEach(System.out::println);
        }
    }


}
