package com.harmony.umbrella.json;

import static org.junit.Assert.*;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.harmony.umbrella.json.serializer.SimplePatternPropertyPreFilter;
import com.harmony.umbrella.json.vo.Person;

/**
 * @author wuxii@foxmail.com
 */
public class FilterTest {

    @Test
    public void excludeTest() {
        SimplePatternPropertyPreFilter filter = new SimplePatternPropertyPreFilter();
        assertEquals("{}", JSON.toJSONString(Person.me, filter));
    }

}
