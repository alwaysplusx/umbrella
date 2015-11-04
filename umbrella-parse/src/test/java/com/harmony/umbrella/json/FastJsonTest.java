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
        JSON.DUMP_CLASS = "./target";
        String json2 = JSON.toJSONString(parent, SerializerFeature.PrettyFormat);
        System.out.println(json2);

    }
}
