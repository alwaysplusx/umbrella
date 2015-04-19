package com.harmony.umbrella.jaxws.cxf.interceptor;

import java.lang.reflect.Method;

import org.apache.cxf.message.Message;

import com.harmony.umbrella.jaxws.Phase;

public class HandleInInterceptor extends AbstractHandleInterceptor {

	// private static final Logger log =
	// LoggerFactory.getLogger(HandleInInterceptor.class);

	public HandleInInterceptor() {
		// super(POST_UNMARSHAL);
		super(Phase.PRE_INVOKE.render());
	}

	@Override
	protected void handleServerValidation(Message message, Object resourceInstance, Method method, Object[] args) {
		// try {
		// Method[] handlMethods = finder.findHandlerMethod(method, getJaxWsPhase());
		// for (Method m : handlMethods) {
		// try {
		// Object target = beanLoader.loadBean(m.getDeclaringClass());
		// invoker.invok(target, m, args);
		// } catch (InvokException e) {
		// log.error("", e);
		// }
		// }
		// } finally {
		// PREVIOUS_SERVER_MESSAGE.set(message);
		// }
	}

	@Override
	protected void handleProxyValidation(Message message, Method method, Object[] responseArgs) {
		// try {
		// Message requestMessage = PREVIOUS_PROXY_MESSAGE.get();
		// if (requestMessage == null) {
		// log.warn("please add handle out interceptor");
		// return;
		// }
		// final Object[] requestArgs = resolverArguments(requestMessage);
		// Method[] handlMethods = finder.findHandlerMethod(method, Phase.POST_INVOKE);
		// List<Object> argList = new ArrayList<Object>();
		// Collections.addAll(argList, responseArgs);
		// Collections.addAll(argList, requestArgs);
		// for (Method m : handlMethods) {
		// Object target = beanLoader.loadBean(m.getDeclaringClass());
		// try {
		// invoker.invok(target, m, argList.toArray());
		// } catch (InvokException e) {
		// log.error("", e);
		// }
		// }
		// } finally {
		// PREVIOUS_PROXY_MESSAGE.set(null);
		// }
	}

}
