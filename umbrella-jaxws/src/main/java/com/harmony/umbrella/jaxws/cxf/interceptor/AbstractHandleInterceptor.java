package com.harmony.umbrella.jaxws.cxf.interceptor;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.core.DefaultInvoker;
import com.harmony.umbrella.core.Invoker;
import com.harmony.umbrella.jaxws.Phase;
import com.harmony.umbrella.jaxws.cxf.CXFMessageUtils;
import com.harmony.umbrella.jaxws.util.JaxWsHandlerMethodFinder;

public abstract class AbstractHandleInterceptor extends AbstractValidationInterceptor {

	private static final Logger log = LoggerFactory.getLogger(AbstractHandleInterceptor.class);
	protected static final ThreadLocal<Message> PREVIOUS_SERVER_MESSAGE = new ThreadLocal<Message>();
	protected static final ThreadLocal<Message> PREVIOUS_PROXY_MESSAGE = new ThreadLocal<Message>();

	private static final Object[] EMPTY_ARGUMENTS = new Object[] {};

	protected BeanFactory beanFactory = new SimpleBeanFactory();
	protected Invoker invoker = new DefaultInvoker();
	protected JaxWsHandlerMethodFinder finder;

	public AbstractHandleInterceptor(String phase) {
		this(phase, "");
	}

	public AbstractHandleInterceptor(String phase, String scanPackage) {
		super(phase);
		finder = new JaxWsHandlerMethodFinder(scanPackage);
	}

	protected abstract void handleServerValidation(Message message, Object resourceInstance, Method method, Object[] args);

	protected abstract void handleProxyValidation(Message message, Method method, Object[] args);

	@Override
	protected void handleValidation(Message message, Object resourceInstance, Method method, List<Object> arguments) {
		Object[] args = resolverArguments(arguments);
		if (resourceInstance instanceof Client) {
			handleProxyValidation(message, method, args);
		} else {
			handleServerValidation(message, resourceInstance, method, args);
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

	public Phase getJaxWsPhase() {
		return Phase.value(getPhase());
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanLoader(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
