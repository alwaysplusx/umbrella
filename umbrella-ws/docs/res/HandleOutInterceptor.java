package com.harmony.umbrella.ws.cxf.interceptor;

import java.lang.reflect.Method;

import org.apache.cxf.message.Message;

import com.harmony.umbrella.ws.Phase;

public class HandleOutInterceptor extends AbstractHandleInterceptor {

	// private static final Logger log =
	// LoggerFactory.getLogger(HandleOutInterceptor.class);

	public HandleOutInterceptor() {
		// super(PRE_MARSHAL);
		super(Phase.POST_INVOKE.render());
	}

	@Override
	protected void handleServer(Message message, Object resourceInstance, Method method, Object[] responseArgs) {
		// Message requestMessage = PREVIOUS_SERVER_MESSAGE.get();
		// if (requestMessage == null) {
		// log.warn("please add handle in interceptor");
		// return;
		// }
		// try {
		// final Object[] requestArgs = resolverArguments(requestMessage);
		// Method[] handlMethods = finder.findHandlerMethod(method, getJaxWsPhase());
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
		// PREVIOUS_SERVER_MESSAGE.set(null);
		// }
	}

	@Override
	protected void handleProxy(Message message, Method method, Object[] args) {
		// try {
		// Method[] handlMethods = finder.findHandlerMethod(method, Phase.PRE_INVOKE);
		// for (Method m : handlMethods) {
		// Object target = beanLoader.loadBean(m.getDeclaringClass());
		// try {
		// invoker.invok(target, m, args);
		// } catch (InvokException e) {
		// log.error("", e);
		// }
		// }
		// } finally {
		// PREVIOUS_PROXY_MESSAGE.set(message);
		// }
	}

}
