package com.harmony.modules.jaxws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.jaxws.Handler.HandleMethod;

@Handler(HelloService.class)
public class HelloServiceHandler {

	private static Logger log = LoggerFactory.getLogger(HelloServiceHandler.class);

	@HandleMethod(phase = Phase.PRE_INVOKE)
	public boolean sayHi(String name) {
		log.info("hello service handler pre invoker, {} say hello", name);
		return true;
	}

	@HandleMethod(phase = Phase.ABORT)
	public void sayHi(JaxWsAbortException e, String name) {

	}

	@HandleMethod(phase = Phase.POST_INVOKE)
	public void sayHi(String result, String name) {

	}

	@HandleMethod(phase = Phase.THROWING)
	public void sayHi(Throwable e, String name) {

	}

	@HandleMethod(phase = Phase.FINALLY)
	public void sayHi(Throwable e, String result, String name) {

	}

}
