package com.harmony.umbrella.json;

import static com.harmony.umbrella.json.JsonTest.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author wuxii@foxmail.com
 */
public class FastJsonTest {

    public static void main(String[] args) {

//        String json = JSON.toJSONString(child1, new SimpleExcludeFilter("parent"), SerializerFeature.PrettyFormat);
//        System.out.println(json);
        JSON.DUMP_CLASS = "./build";
        String json2 = JSON.toJSONString(parent, SerializerFeature.PrettyFormat);
        System.out.println(json2);

    }
}
