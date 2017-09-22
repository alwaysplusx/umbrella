package com.harmony.umbrella.ws.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationContext.ClassResource;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ProxyExecutor;
import com.harmony.umbrella.ws.WebServiceAbortException;
import com.harmony.umbrella.ws.annotation.Syncable;
import com.harmony.umbrella.ws.proxy.Proxy;
import com.harmony.umbrella.ws.proxy.ProxyCallback;

/**
 * 业务回调的扩展周期访问者
 * <p>
 * 基于 {@linkplain Syncable}, {@linkplain ProxyCallback}的功能扩展，
 * {@linkplain SyncableContextVisitor}加载类路径下的标注有{@linkplain Syncable} 注解的类（注有
 * {@linkplain Syncable}表示一个可同步的业务bean）。
 * <p>
 * {@linkplain ProxyCallback}接口的实现类对注有{@linkplain Syncable} 的同步业务bean起到回调作用。将
 * {@linkplain SyncableContextVisitor}注入到{@linkplain ProxyExecutor}中则客户实现对
 * {@linkplain ProxyCallback}的实现在同步业务上的周期回调
 * 
 * @author wuxii@foxmail.com
 */
public class SyncableContextVisitor extends AbstractContextVisitor {

    private static final Log log = Logs.getLog(SyncableContextVisitor.class);

    /**
     * 用户扫描类路径下的{@linkplain Syncable}
     */
    private CallbackFinder callbackFinder;

    /**
     * 负责初始化回调的{@linkplain ProxyCallback}
     */
    private BeanFactory beanFactory;

    public SyncableContextVisitor() {
    }

    public SyncableContextVisitor(BeanFactory beanFactory) {
        this.callbackFinder = new CallbackFinder();
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean visitBefore(Context context) throws WebServiceAbortException {
        Object obj = context.get(Proxy.SYNC_OBJECT);
        Map<String, Object> content = context.getContextMap();

        for (ProxyCallback callback : getCallbacks(context)) {
            for (Object o : asList(obj)) {
                callback.forward(o, content);
            }
        }

        return true;
    }

    @Override
    public void visitCompletion(Object result, Context context) {
        Object obj = context.get(Proxy.SYNC_OBJECT);
        Map<String, Object> content = context.getContextMap();
        for (ProxyCallback callback : getCallbacks(context)) {
            for (Object o : asList(obj)) {
                callback.success(o, result, content);
            }
        }
    }

    @Override
    public void visitThrowing(Throwable throwable, Context context) {
        Object obj = context.get(Proxy.SYNC_OBJECT);
        Map<String, Object> content = context.getContextMap();
        for (ProxyCallback callback : getCallbacks(context)) {
            for (Object o : asList(obj)) {
                callback.failed(o, throwable, content);
            }
        }
    }

    public void setCallbackFinder(CallbackFinder callbackFinder) {
        this.callbackFinder = callbackFinder;
    }

    /*
     * 延迟加载
     */
    protected BeanFactory getBeanFactory() {
        if (beanFactory == null) {
            beanFactory = ApplicationContext.getApplicationContext();
        }
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private List asList(Object obj) {
        return obj instanceof Collection ? new ArrayList((Collection) obj) : Arrays.asList(obj);
    }

    private List<ProxyCallback> getCallbacks(Context context) {
        List<ProxyCallback> result = new ArrayList<ProxyCallback>();
        Class<ProxyCallback>[] classes = callbackFinder.getCallbackClasses(context.getServiceClass(), context.getMethodName());
        if (classes != null && classes.length > 0) {
            BeanFactory beanFactory = getBeanFactory();
            for (Class<ProxyCallback> callbackClass : classes) {
                result.add(beanFactory.getBean(callbackClass));
            }
        }
        return result;
    }

    /**
     * 通过扫描类路径下的类，过滤标注有{@linkplain Syncable}注解的类
     * 
     * @author wuxii@foxmail.com
     */
    public class CallbackFinder {

        /**
         * 所有标注了syncable注解的class
         */
        private Class<? extends ProxyCallback>[] callbacks;

        /**
         * 各个类对应的{@linkplain ProxyCallback}缓存
         */
        private final Map<String, List> callbackMap = new HashMap<String, List>();

        /**
         * 加载类路径下所有标注有{@linkplain Syncable} 的{@linkplain ProxyCallback}
         * 且服务类是serviceClass，对于的同步方法为methodName的类
         * 
         * @param serviceClass
         *            匹配的服务类
         * @param methodName
         *            服务方法
         * @return 对应的SyncCallback
         */
        public Class<ProxyCallback>[] getCallbackClasses(Class<?> serviceClass, String methodName) {
            String key = serviceClass.getName() + "#" + methodName;
            List classes = callbackMap.get(key);
            if (classes == null) {
                classes = new ArrayList<Class>();
                for (Class<? extends ProxyCallback> clazz : callbacks()) {
                    if (isMatchCallback(clazz, serviceClass, methodName)) {
                        classes.add(clazz);
                    }
                }
                callbackMap.put(key, classes);
                log.debug("{}, all callback {}", key, classes);
            }
            return (Class<ProxyCallback>[]) classes.toArray(new Class[classes.size()]);
        }

        protected Class<? extends ProxyCallback>[] callbacks() {
            if (callbacks == null) {
                callbacks = getAllCallbackClass();
            }
            return callbacks;
        }

        public void clear() {
            this.callbacks = null;
            this.callbackMap.clear();
        }

        private Class<? extends ProxyCallback>[] getAllCallbackClass() {
            List<Class<? extends ProxyCallback>> result = new ArrayList<>();
            ClassResource[] resources = ApplicationContext.getApplicationClassResources();
            for (ClassResource res : resources) {
                Class<?> clazz = res.forClass();
                if (clazz != null //
                        && !result.contains(clazz)//
                        && ProxyCallback.class.isAssignableFrom(clazz) //
                        && clazz.getAnnotation(Syncable.class) != null) {
                    result.add((Class<? extends ProxyCallback>) clazz);
                }
            }
            return result.toArray(new Class[result.size()]);
        }

        /*
         * 匹配比较，注解中的名称，服务接口类
         */
        protected boolean isMatchCallback(Class<?> clazz, Class<?> serviceClass, String methodName) {
            Syncable ann = clazz.getAnnotation(Syncable.class);
            return ann.endpoint().isAssignableFrom(serviceClass) && ann.methodName().equals(methodName);
        }

    }

}
