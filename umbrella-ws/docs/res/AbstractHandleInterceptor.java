package com.harmony.umbrella.ws.cxf.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.invoker.MethodDispatcher;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.validation.AbstractValidationInterceptor;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.DefaultInvoker;
import com.harmony.umbrella.core.Invoker;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.ws.Phase;
import com.harmony.umbrella.ws.cxf.CXFMessageUtils;
import com.harmony.umbrella.ws.util.HandlerMethodFinder;

/**
 * {@linkplain AbstractValidationInterceptor}扩展
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractHandleInterceptor extends AbstractValidationInterceptor {

    private static final Log log = Logs.getLog(AbstractHandleInterceptor.class);
    protected static final ThreadLocal<Message> PREVIOUS_SERVER_MESSAGE = new ThreadLocal<Message>();
    protected static final ThreadLocal<Message> PREVIOUS_PROXY_MESSAGE = new ThreadLocal<Message>();

    private static final Object[] EMPTY_ARGUMENTS = new Object[] {};

    protected BeanFactory beanFactory = new SimpleBeanFactory();

    protected Invoker invoker = new DefaultInvoker();

    protected HandlerMethodFinder finder;

    public AbstractHandleInterceptor(String phase) {
        this(phase, "");
    }

    public AbstractHandleInterceptor(String phase, String scanPackage) {
        super(phase);
        finder = new HandlerMethodFinder(scanPackage);
    }

    /**
     * 处理作为服务端的服务消息
     * 
     * @param message
     * @param resourceInstance
     * @param method
     * @param args
     */
    protected abstract void handleServer(Message message, Object resourceInstance, Method method, Object[] args);

    /**
     * 处理作为客户代理端的消息
     * 
     * @param message
     * @param method
     * @param args
     */
    protected abstract void handleProxy(Message message, Method method, Object[] args);

    @Override
    protected void handleValidation(Message message, Object resourceInstance, Method method, List<Object> arguments) {
        Object[] args = resolverArguments(arguments);
        if (resourceInstance instanceof Client) {
            handleProxy(message, method, args);
        } else {
            handleServer(message, resourceInstance, method, args);
        }
    }

    protected final Object[] resolverArguments(Message message) {
        List<Object> list = MessageContentsList.getContentsList(message);
        return resolverArguments(list);
    }

    protected static Object[] resolverArguments(List<Object> arguments) {
        if (arguments == null || arguments.isEmpty())
            return EMPTY_ARGUMENTS;
        return reflectArguments(arguments.get(0));
    }

    private static Object[] reflectArguments(Object arg) {
        try {
            Class.forName(arg.getClass().getName());
            return new Object[] { arg };
        } catch (ClassNotFoundException e) {
            Class<?> asmClass = arg.getClass();
            Field[] asmFields = asmClass.getDeclaredFields();
            Object[] parameters = new Object[asmFields.length];
            for (int i = 0; i < asmFields.length; i++) {
                asmFields[i].setAccessible(true);
                try {
                    parameters[i] = asmFields[i].get(arg);
                } catch (Exception e1) {
                }
            }
            return parameters;
        }
    }

    protected final boolean isCurrentPhase(Phase phase) {
        return getPhase().equals(phase.render());
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        final Object theServiceObject = getServiceObject(message);
        if (theServiceObject == null) {
            log.warn("service object is null, skip handle validation");
            return;
        }

        final Method method = getServiceMethod(message);
        if (method == null) {
            log.warn("service method is null, skip handle validation");
            return;
        }

        final List<Object> arguments = MessageContentsList.getContentsList(message);
        handleValidation(message, theServiceObject, method, arguments);
    }

    @Override
    protected Method getServiceMethod(Message message) {
        Message inMessage = message.getExchange().getInMessage();
        Method method = null;
        if (inMessage != null) {
            method = (Method) inMessage.get("org.apache.cxf.resource.method");
            if (method == null) {
                BindingOperationInfo bop = inMessage.getExchange().get(BindingOperationInfo.class);
                if (bop != null) {
                    MethodDispatcher md = (MethodDispatcher) inMessage.getExchange().get(Service.class).get(MethodDispatcher.class.getName());
                    method = md.getMethod(bop);
                }
            }
        }
        if (method == null)
            method = CXFMessageUtils.getRequestMethod(message);
        return method;
    }

    @Override
    protected Object getServiceObject(Message message) {
        Object target = null;
        if ((target = super.getServiceObject(message)) != null)
            return target;
        Client client = message.getExchange().get(Client.class);
        return client;
    }

    /**
     * 执行所处的周期
     * 
     * @return
     */
    public Phase getJaxWsPhase() {
        return Phase.value(getPhase());
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}
