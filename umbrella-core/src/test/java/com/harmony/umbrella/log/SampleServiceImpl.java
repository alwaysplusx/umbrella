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
//import java.lang.reflect.Proxy;
//
//import org.junit.Test;
//
//import com.harmony.umbrella.log.proxy.SampleServiceProxy;
//
///**
// * @author wuxii@foxmail.com
// */
//public class SampleServiceImpl implements SampleService {
//
//    @Override
//    public String save(SampleEntity entity) {
//        return "success";
//    }
//
//    @Test
//    public void testProxyLog() {
//        ClassLoader cl = Thread.currentThread().getContextClassLoader();
//
//        SampleService proxy = (SampleService) Proxy.newProxyInstance(cl, new Class[] { SampleService.class }, new SampleServiceProxy(new SampleServiceImpl()));
//
//        proxy.save(new SampleEntity(1l));
//    }
//}
