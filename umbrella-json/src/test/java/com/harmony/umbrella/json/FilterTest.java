package com.harmony.umbrella.json;

import static org.junit.Assert.*;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.harmony.umbrella.json.serializer.SimpleAnnotationFilter;
import com.harmony.umbrella.json.serializer.SimplePatternFilter;
import com.harmony.umbrella.json.vo.Person;

/**
 * @author wuxii@foxmail.com
 */
public class FilterTest {

    @Test
    public void excludeTest() {
        SimplePatternFilter filter = new SimplePatternFilter(FilterMode.INCLUDE);
        assertEquals("{}", JSON.toJSONString(Person.me, filter));
    }

    @Test
    public void annotationTest() {
        SimpleAnnotationFilter filter = new SimpleAnnotationFilter(FilterMode.INCLUDE);
        assertEquals("{}", JSON.toJSONString(Person.me, filter));
    }

}
