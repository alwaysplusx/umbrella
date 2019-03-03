package com.harmony.umbrella.web;

import com.harmony.umbrella.json.Json;
import org.junit.Test;

/**
 * @author wuxii
 */
public class ResponseTest {

    @Test
    public void deserializer() {
        Response<Object> response = Response.newBuilder(Response.ERROR).desc("错误的返回").build();
        String jsonText = Json.toJson(response);
        System.out.println(jsonText);
    }

}
