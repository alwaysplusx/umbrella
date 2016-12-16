package com.harmony.umbrella.json.builder;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.json.SerializerConfig;
import com.harmony.umbrella.json.builder.SerializerConfigBuilderFactory.SerializerConfigBuilder;
import com.harmony.umbrella.json.tsf.AutoPrefixPropertyTransformer;
import com.harmony.umbrella.json.vo.Person;

/**
 * @author wuxii@foxmail.com
 */
public class SerializerConfigBuilderFactoryTest {

    private static SerializerConfigBuilderFactory builderFactory;

    @BeforeClass
    public static void beforeClass() {
        builderFactory = new SerializerConfigBuilderFactory();
        AutoPrefixPropertyTransformer tsf = new AutoPrefixPropertyTransformer();
        tsf.map(Object[].class, "[*]");
        tsf.map(Collection.class, "[*]");
        builderFactory.addTypedPropertyTransformers(tsf);
    }

    @Test
    public void test() {
        SerializerConfigBuilder builder = builderFactory.configFor(Person.class);
        SerializerConfig config = builder//
                .withProperty("username", "password", "childs", "brothers")//
                .withFeature(SerializerFeature.PrettyFormat)//
                .build();
        System.out.println(Json.toJson(Person.me, config));
    }

}
