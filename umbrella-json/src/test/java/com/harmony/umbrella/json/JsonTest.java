package com.harmony.umbrella.json;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.vo.Person;

/**
 * @author wuxii@foxmail.com
 */
public class JsonTest {

    @Test
    public void testToJsonObjectSerializerFeatureArray() {
        String json = Json.toJson(Person.me, PrettyFormat, QuoteFieldNames);
        System.out.println(json);
    }

    @Test
    public void testFromJson() {
        String json = "{a: 'b'}";
        Map<String, Object> map = Json.parseMap(json);
        assertEquals(map.get("a"), "b");
    }

    @Test
    public void testNameMapped() {
        SerializerConfigBuilder builder = SerializerConfigBuilder.newBuilder();
        builder//
                .setKeyStyle(KeyStyle.UNDER_LINE)//
                .addFeatures(SerializerFeature.PrettyFormat)//
                .setNameMapping(Person.class)//
                .addMapping("personId", "userId");//
        String json = builder.toJson(Person.me);
        System.out.println(json);
    }

}
