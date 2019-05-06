package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.context.CurrentUser;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.annotation.Scope;
import com.harmony.umbrella.log.serializer.LogSerializer;
import com.harmony.umbrella.log.support.StringLogSerializer;
import com.harmony.umbrella.template.ExpressionParser;
import com.harmony.umbrella.template.Template;
import com.harmony.umbrella.template.TemplateResolver;
import com.harmony.umbrella.template.spel.SpelTemplateResolver;
import com.harmony.umbrella.template.support.DefaultExpressionParser;
import com.harmony.umbrella.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxii
 */
public class LogInterceptor extends AbstractLogInterceptor implements InitializingBean, BeanFactoryAware {

    private ExpressionParser expressionParser;
    private TemplateResolver templateResolver;
    private LogSerializer logSerializer;

    private BeanFactory beanFactory;

    @Override
    protected void invokeWithLogging(LogOperation logOperation, LogInterceptorContext logInterceptorContext) {
        LogMessageContext messageContext = buildMessageContext(logOperation);

        messageContext.handle(Scope.IN, buildEvaluationContext(logInterceptorContext));
        logInterceptorContext.proceed();
        messageContext.handle(Scope.OUT, buildEvaluationContext(logInterceptorContext));

        LogMessage logMessage = new LogMessage()
                .start(logInterceptorContext.getRequestTime())
                .finish(logInterceptorContext.getResponseTime())
                .module(logOperation.getModule())
                .action(logOperation.getAction())
                .level(logOperation.getLevel())
                .error(logInterceptorContext.getError())
                .message(messageContext.getFormattedMessage())
                .key(messageContext.getFormattedKey())
                .traceId(logInterceptorContext.getTraceContext().getTraceId())
                .threadFrame(StringUtils.getMethodId(logInterceptorContext.getMethod()))
                .currentThread();

        CurrentUser currentUser = getCurrentUser();
        if (currentUser != null) {
            logMessage.userId(currentUser.getUserId())
                    .username(currentUser.getUsername());
        }

        Class<?> declaringClass = logInterceptorContext.getMethod().getDeclaringClass();
        logMessage.log(LoggerFactory.getLogger(declaringClass), logSerializer);
    }

    protected CurrentUser getCurrentUser() {
        CurrentContext cc = ContextHelper.getCurrentContext();
        return cc != null ? cc.getPrincipals() : null;
    }

    protected Object buildEvaluationContext(LogInterceptorContext logInterceptorContext) {
        return new LogEvaluationContext(
                logInterceptorContext,
                logInterceptorContext.getMethod(),
                logInterceptorContext.getArgs()
        );
    }

    protected LogMessageContext buildMessageContext(LogOperation logOperation) {
        List<ScopeExpression> expressions = logOperation.parseMessage(expressionParser);
        List<TemplateHolder> messageTemplates = new ArrayList<>();
        for (ScopeExpression expression : expressions) {
            Template<ScopeExpression> template = templateResolver.resolve(expression);
            messageTemplates.add(new TemplateHolder(template));
        }
        ScopeExpression keyExpression = logOperation.getKeyExpression();
        TemplateHolder keyTemplate = keyExpression != null
                ? new TemplateHolder(templateResolver.resolve(keyExpression))
                : null;
        return new LogMessageContext(messageTemplates, keyTemplate);
    }

    public void setExpressionParser(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public void setLogSerializer(LogSerializer logSerializer) {
        this.logSerializer = logSerializer;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (logSerializer == null) {
            logSerializer = getBean(LogSerializer.class, StringLogSerializer.INSTANCE);
        }
        if (templateResolver == null) {
            templateResolver = getBean(TemplateResolver.class, new SpelTemplateResolver());
        }
        if (expressionParser == null) {
            expressionParser = getBean(ExpressionParser.class, new DefaultExpressionParser());
        }
    }

    private <T> T getBean(Class<T> requestType, T def) {
        try {
            if (beanFactory == null) {
                return def;
            }
            return beanFactory.getBean(requestType);
        } catch (BeansException e) {
            return def;
        }
    }

}
