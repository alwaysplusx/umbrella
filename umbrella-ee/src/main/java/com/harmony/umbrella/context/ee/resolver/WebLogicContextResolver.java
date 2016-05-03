//package com.harmony.umbrella.context.ee.resolver;
//
//import java.util.HashSet;
//import java.util.Properties;
//import java.util.Set;
//
//import com.harmony.umbrella.log.Log;
//import com.harmony.umbrella.log.Logs;
//import com.harmony.umbrella.context.ee.WrappedBeanHandler;
//
///**
// * @author wuxii@foxmail.com
// */
//public class WebLogicContextResolver extends InternalContextResolver {
//
//    private static final Log log = Logs.getLog(WebLogicContextResolver.class);
//    private static final Set<WrappedBeanHandler> handlers = new HashSet<WrappedBeanHandler>();
//
//    static {
//
//        final Set<Class<?>> wrapClasses = new HashSet<Class<?>>();
//
//        try {
//            wrapClasses.add(Class.forName("weblogic.ejb.container.internal.SessionEJBContextImpl"));
//        } catch (ClassNotFoundException e1) {
//        }
//
//        handlers.add(new WrappedBeanHandler() {
//
//            @Override
//            public Object unwrap(Object bean) {
//                try {
//                    return bean.getClass().getMethod("getBean").invoke(bean);
//                } catch (Exception e) {
//                    log.warn("not weblogic wrap bean");
//                }
//                return null;
//            }
//
//            @Override
//            public boolean isWrappedBean(Object bean) {
//                Class<?> beanClass = bean.getClass();
//                for (Class<?> clazz : wrapClasses) {
//                    if (clazz.isAssignableFrom(beanClass)) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//    }
//
//    public WebLogicContextResolver(Properties props) {
//        super(props);
//        this.wrappedBeanHandlers.addAll(handlers);
//    }
//
//    /*@Override
//    public SessionBean search(BeanDefinition beanDefinition, Context context) {
//        return null;
//    }*/
//
//    /*@Override
//    public void clear() {
//        super.clear();
//    }*/
//
//}
