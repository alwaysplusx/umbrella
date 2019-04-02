package com.harmony.umbrella.lock.interceptor;

import com.harmony.umbrella.lock.ConfigurableLockRegistry;
import com.harmony.umbrella.lock.annotation.Lock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author wuxii
 */
public class LockInterceptor implements MethodInterceptor, BeanFactoryAware {

	private ConfigurableLockRegistry lockRegistry;

	private ExpressionParser expressionParser = new SpelExpressionParser();

	private BeanFactory beanFactory;

	public LockInterceptor() {
	}

	public LockInterceptor(ConfigurableLockRegistry lockRegistry) {
		this.lockRegistry = lockRegistry;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		LockOperation lockAttribute = getLockAttribute(invocation.getMethod());
		if (lockAttribute == null) {
			return invocation.proceed();
		}
		Object lockKey = lockIdentificationKey(lockAttribute, invocation);
		int timeout = lockAttribute.getTimeout();
		java.util.concurrent.locks.Lock lock = lockRegistry.obtain(lockKey, timeout);
		try {
			lock.lock();
			return invocation.proceed();
		} finally {
			lock.unlock();
		}
	}

	protected Object lockIdentificationKey(LockOperation lockOperation, MethodInvocation invocation) {
		// TODO beanFactory to load keyGenerate bean
		StringBuilder lockKeyBuf = new StringBuilder(lockOperation.getName());
		String key = lockOperation.getKey();
		if (StringUtils.hasText(key)) {
			// TODO create EvaluationContext
			lockKeyBuf.append(expressionParser.parseExpression(key).getValue());
		}
		return lockKeyBuf.toString();
	}

	private LockOperation getLockAttribute(Method method) {
		Lock ann = AnnotationUtils.findAnnotation(method, Lock.class);
		if (ann == null) {
			return null;
		}
		return LockOperation
				.builder()
				.name(ann.name())
				.key(ann.key())
				.timeout(ann.timeout())
				.keyGenerator(ann.keyGenerator())
				.build();
	}

	public void setLockRegistry(ConfigurableLockRegistry lockRegistry) {
		this.lockRegistry = lockRegistry;
	}

	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
