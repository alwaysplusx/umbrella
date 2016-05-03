///*
// * Copyright 2002-2014 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.harmony.umbrella.log;
//
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.harmony.umbrella.log.annotation.Logging;
//import com.harmony.umbrella.log.template.MessageTemplateFactory;
//import com.harmony.umbrella.log.template.Template;
//
///**
// * @author wuxii@foxmail.com
// */
//public class TemplateTest {
//
//    private static Method method;
//    static {
//        try {
//            method = TemplateTest.class.getMethod("sayHi", SampleEntity.class, Map.class);
//        } catch (Exception e) {
//        }
//    }
//
//    public static void main(String[] args) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("key", "wuxii");
//
//        MessageTemplateFactory mtf = new MessageTemplateFactory();
//        Template tmp = mtf.createTemplate(method);
//        Message message = tmp.newMessage(new TemplateTest(), "success", new Object[] { new SampleEntity(1l), map });
//
//        System.out.println(message.getFormattedMessage());
//    }
//
//    @Logging(message = "保存数据【{0[key].sampleId}】, {result}", id = "{0.sampleId}")
//    public String sayHi(SampleEntity entity, Map<String, Object> params) {
//        return "success";
//    }
//}
