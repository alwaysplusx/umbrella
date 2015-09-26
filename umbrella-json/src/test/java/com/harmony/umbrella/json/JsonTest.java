/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.json;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;

import java.util.Arrays;

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

}
