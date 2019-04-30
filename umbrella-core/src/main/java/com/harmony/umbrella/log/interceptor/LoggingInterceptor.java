package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.context.CurrentUser;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.annotation.Scope;
import com.harmony.umbrella.log.serializer.LogSerializer;
import com.harmony.umbrella.template.ExpressionParser;
import com.harmony.umbrella.template.Template;
import com.harmony.umbrella.template.TemplateResolver;
import com.harmony.umbrella.template.spel.SpelTemplateResolver;
import com.harmony.umbrella.template.support.DefaultExpressionParser;
import com.harmony.umbrella.util.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxii
 */
public class LoggingInterceptor extends AbstractLogInterceptor {

    private ExpressionParser expressionParser = new DefaultExpressionParser();
    private TemplateResolver templateResolver = new SpelTemplateResolver();
    private LogSerializer logSerializer;

    @Override
    protected void invokeWithLogging(LoggingOperation loggingOperation, LogInterceptorContext logInterceptorContext) {
        LogMessageContext messageContext = buildMessageContext(loggingOperation);

        messageContext.handle(Scope.IN, buildEvaluationContext(logInterceptorContext));
        logInterceptorContext.proceed();
        messageContext.handle(Scope.OUT, buildEvaluationContext(logInterceptorContext));

        LogMessage logMessage = new LogMessage()
                .start(logInterceptorContext.getRequestTime())
                .finish(logInterceptorContext.getResponseTime())
                .module(loggingOperation.getModule())
                .action(loggingOperation.getAction())
                .level(loggingOperation.getLevel())
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
        return new LoggingEvaluationContext(
                logInterceptorContext,
                logInterceptorContext.getMethod(),
                logInterceptorContext.getArgs()
        );
    }

    protected LogMessageContext buildMessageContext(LoggingOperation loggingOperation) {
        List<ScopeExpression> expressions = loggingOperation.parseMessage(expressionParser);
        List<TemplateHolder> messageTemplates = new ArrayList<>();
        for (ScopeExpression expression : expressions) {
            Template<ScopeExpression> template = templateResolver.resolve(expression);
            messageTemplates.add(new TemplateHolder(template));
        }
        ScopeExpression keyExpression = loggingOperation.getKeyExpression();
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
}
