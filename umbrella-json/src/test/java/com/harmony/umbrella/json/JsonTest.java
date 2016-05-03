package com.harmony.umbrella.json;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.harmony.umbrella.json.vo.Child;
import com.harmony.umbrella.json.vo.Parent;

/**
 * @author wuxii@foxmail.com
 */
public class JsonTest {

    public static final Parent parent;
    public static final Child child1;
    public static final Child child2;

    static {
        parent = new Parent(1l, "wuxii");
        child1 = new Child(1l, "child1", parent);
        child2 = new Child(2l, "child2", parent);
        parent.setChilds(Arrays.asList(child1, child2));
    }

    @Test
    public void testToJsonObject() {
        System.out.println(Json.toJson(parent));
    }

    @Test
    public void testToJsonObjectSerializerFeatureArray() {
        String json = Json.toJson(parent, PrettyFormat, QuoteFieldNames);
        System.out.println(json);
    }

    @Test
    public void testFromJson() {
        String json = "{a: 'b'}";
        Map<String, Object> map = Json.toMap(json);
        assertEquals(map.get("a"), "b");
    }

}
