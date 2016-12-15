package com.harmony.umbrella.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.vo.Person;

/**
 * @author wuxii@foxmail.com
 */
public class FastJsonTest {

    public static void main(String[] args) {

        //        String json = JSON.toJSONString(child1, new SimpleExcludeFilter("parent"), SerializerFeature.PrettyFormat);
        //        System.out.println(json);
        JSON.DUMP_CLASS = "./build";
        String json2 = JSON.toJSONString(Person.me, SerializerFeature.PrettyFormat);
        System.out.println(json2);

    }
}
