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
//import java.io.IOException;
//import java.lang.reflect.Proxy;
//
//import org.junit.Test;
//
//import com.harmony.umbrella.log.proxy.SampleServiceProxy;
//
///**
// * @author wuxii@foxmail.com
// */
//public class LogPrinter {
//
//    static Log log = Logs.getLog(LogPrinter.class);
//
//    public static void main(String[] args) {
//        
//        log.info("do save success, data is {}", "123123123");
//        
//        log.error(new Exception("this is wrong"));
//
//        LogMessage.create(log)
//        .bizId(1L)
//        .bizModule("Object")
//        .operator("wuxii")
//        .operatorId("1")
//        .action("保存")
//        .module("Sample")
//        .level(Level.INFO)
//        .currentStack()
//        .currentThread()
//        .message("保存成功【{}, {}】成功", "10293819").log();
//    }
//
//    @Test
//    public void logTest() {
//        SampleService service = (SampleService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { SampleService.class },
//                new SampleServiceProxy(new SampleServiceImpl()));
//
//        service.save(new SampleEntity(123l));
//    }
//    
//    @Test
//    public void formatTest() throws IOException {
//        LogInfo info = LogMessage.create(log)
//                .bizId(1L)
//                .bizModule("Object")
//                .operator("wuxii")
//                .operatorId("1")
//                .action("保存")
//                .module("Sample")
//                .level(Level.INFO)
//                .currentStack()
//                .currentThread()
//                .message("保存成功【{}, {}】成功", "10293819").asInfo();
//        
//        System.out.println(info);
//        
//    }
//
//}
