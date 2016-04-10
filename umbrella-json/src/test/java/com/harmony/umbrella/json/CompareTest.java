///*
// * Copyright 2012-2016 the original author or authors.
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
//package com.harmony.umbrella.json;
//
//import java.io.IOException;
//import java.io.StringWriter;
//import java.math.BigDecimal;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//import org.junit.Test;
//
//import com.alibaba.fastjson.JSON;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.harmony.umbrella.json.vo.Foo;
//
///**
// * @author wuxii@foxmail.com
// */
//public class CompareTest {
//
//    private static final int count = 1000000;
//    private static final List<Foo> foos = new ArrayList<Foo>();
//
//    static {
//        for (int i = 0; i < count; i++) {
//            foos.add(newFoo());
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
//        // new CompareTest().testFastjson();
//        new CompareTest().testJackson();
//    }
//
//    public void testJackson() throws IOException {
//        ObjectMapper om = new ObjectMapper();
//        long startTime = System.currentTimeMillis();
//        for (Foo foo : foos) {
//            om.writeValue(new StringWriter(), foo);
//        }
//        System.out.println("Jackson use:" + (System.currentTimeMillis() - startTime) + "ms");
//    }
//
//    @Test
//    public void testFastjson() {
//        long startTime = System.currentTimeMillis();
//        for (Foo foo : foos) {
//            JSON.toJSONString(foo);
//        }
//        System.out.println("Fastjson use:" + (System.currentTimeMillis() - startTime) + "ms");
//    }
//
//    private static Foo newFoo() {
//        Foo foo = new Foo();
//        foo.setA(UUID.randomUUID().toString());
//        foo.setB((int) Math.random());
//        foo.setC((char) Math.random());
//        foo.setD((short) Math.random());
//        foo.setE(new Date());
//        foo.setF(Calendar.getInstance());
//        foo.setG(Math.round(Math.random()));
//        foo.setH((float) Math.random());
//        foo.setI(Math.random());
//        foo.setJ(BigDecimal.valueOf(Math.random()));
//        foo.setK(Math.round(Math.random()));
//        foo.setL((int) Math.round(Math.random()));
//        foo.setM(true);
//        foo.setN(UUID.randomUUID().toString());
//        foo.setO(UUID.randomUUID().toString());
//        foo.setP(UUID.randomUUID().toString());
//        foo.setQ(UUID.randomUUID().toString());
//        foo.setR(UUID.randomUUID().toString());
//        foo.setS((int) Math.round(Math.random()));
//        foo.setT((int) Math.round(Math.random()));
//        foo.setU((int) Math.round(Math.random()));
//        foo.setV((int) Math.round(Math.random()));
//        foo.setW((int) Math.round(Math.random()));
//        foo.setX(new Timestamp(System.currentTimeMillis()));
//        foo.setY(UUID.randomUUID().toString());
//        foo.setZ(new Timestamp(System.currentTimeMillis()));
//        return foo;
//    }
//
//}
