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
