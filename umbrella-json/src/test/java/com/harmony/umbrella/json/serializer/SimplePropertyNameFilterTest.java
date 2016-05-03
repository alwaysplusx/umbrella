package com.harmony.umbrella.json.serializer;

import static com.harmony.umbrella.json.JsonTest.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author wuxii@foxmail.com
 */
public class SimplePropertyNameFilterTest {

    public static void main(String[] args) {

        SimplePropertyNameFilter filter = new SimplePropertyNameFilter("id");
        String json = JSON.toJSONString(child1, filter, SerializerFeature.PrettyFormat);
        System.out.println(json);

    }
}
