//package com.harmony.umbrella.el.resolver;
//
//import java.lang.reflect.Field;
//
//import com.harmony.umbrella.el.CheckedResolver;
//import com.harmony.umbrella.util.ReflectionUtils;
//import com.harmony.umbrella.util.StringUtils;
//
///**
// * @author wuxii@foxmail.com
// */
//public class ClassFieldResolver extends CheckedResolver<Class<?>> {
//
//    @Override
//    protected boolean support(String name, Object obj) {
//        return StringUtils.isNotBlank(name) && obj instanceof Class && ReflectionUtils.findField((Class<?>) obj, name) != null;
//    }
//
//    @Override
//    protected Object doResolve(String name, Class<?> obj) {
//        Field field = ReflectionUtils.findField(obj, name);
//        if (field == null) {
//            throw new IllegalArgumentException(obj + " no such member " + name);
//        }
//        return field;
//    }
//
//}
