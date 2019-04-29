package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.annotation.KeyExpression;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Module;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public abstract class AbstractLogInterceptor implements MethodInterceptor {

    private LogInterceptorFilter logInterceptorFilter;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        LoggingOperation loggingOperation;
        if (logInterceptorFilter == null
                || !logInterceptorFilter.accept(invocation)
                || (loggingOperation = getLoggingOperation(invocation)) == null) {
            return invocation.proceed();
        }
        return invokeWithLogging(loggingOperation, new LogInterceptorContext(invocation));
    }

    protected abstract Object invokeWithLogging(LoggingOperation loggingOperation, LogInterceptorContext logInterceptorContext);

    protected LoggingOperation getLoggingOperation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Logging loggingAnn = AnnotationUtils.getAnnotation(method, Logging.class);
        if (loggingAnn == null) {
            return null;
        }

        Module moduleAnn = AnnotationUtils.getAnnotation(method, Module.class);
        String module = StringUtils.hasText(loggingAnn.module())
                ? loggingAnn.module()
                : moduleAnn != null
                ? moduleAnn.value()
                : null;

        KeyExpression keyAnn = AnnotationUtils.getAnnotation(method, KeyExpression.class);
        ExpressionOperation keyExpressionOperation = keyAnn != null
                ? new ExpressionOperation(keyAnn)
                : StringUtils.hasText(loggingAnn.key())
                ? new ExpressionOperation(loggingAnn.key(), loggingAnn.keyScope(), loggingAnn.key())
                : null;

        return LoggingOperation
                .builder()
                .keyExpression(keyExpressionOperation)
                .module(module)
                .action(loggingAnn.action())
                .message(loggingAnn.message())
                .level(loggingAnn.level())
                .binds(buildBinds(loggingAnn.binds()))
                .build();
    }

    private Map<String, ExpressionOperation> buildBinds(Logging.Expression[] binds) {
        return Stream.of(binds).collect(Collectors.toMap(Logging.Expression::bind, ExpressionOperation::new));
    }

    public void setLogInterceptorFilter(LogInterceptorFilter logInterceptorFilter) {
        this.logInterceptorFilter = logInterceptorFilter;
    }

}
