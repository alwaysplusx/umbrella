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
