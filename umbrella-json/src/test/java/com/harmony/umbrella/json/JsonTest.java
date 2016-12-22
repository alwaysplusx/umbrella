package com.harmony.umbrella.json;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.harmony.umbrella.json.serializer.MyNameFilter;
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
    public void testNameFilter() {
        System.out.println(Json.toJson(Person.me, new SerializeFilter[] { new MyNameFilter() }, PrettyFormat));
    }

}
