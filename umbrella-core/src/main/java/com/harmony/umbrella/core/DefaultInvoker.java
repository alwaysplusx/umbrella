package com.harmony.umbrella.core;

import static com.harmony.umbrella.util.ClassUtils.*;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.harmony.umbrella.util.Exceptions;

/**
 * 默认反射执行工具
 * 
 * @author wuxii@foxmail.com
 */
public class DefaultInvoker implements Invoker, Serializable {

	private static final long serialVersionUID = 5408528044216944899L;
	
	/**
	 * 反射调用前是否进行检验
	 */
	private boolean validate = false;

	@Override
	public Object invoke(Object obj, Method method, Object[] args) throws InvokeException {
		if (validate) {
			Class<?>[] pattern = method.getParameterTypes();
			if (args.length != pattern.length) {
				throw new InvokeException("parameter length mismatch");
			}
			if (!isAssignable(pattern, toTypeArray(args))) {
				throw new InvokeException("parameter type mismatch");
			}
		}
		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			Throwable cause = Exceptions.getRootCause(e);
			throw new InvokeException(cause.getMessage(), cause);
		}
	}

	public boolean isValidate() {
		return validate;
	}

	/**
	 * 设置是否调用前检验
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
	}
}
