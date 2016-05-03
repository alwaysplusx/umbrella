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
