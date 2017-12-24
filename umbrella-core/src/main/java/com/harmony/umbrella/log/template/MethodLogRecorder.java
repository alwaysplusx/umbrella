package com.harmony.umbrella.log.template;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.core.ObjectFormatter;
import com.harmony.umbrella.core.ObjectSerializer;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Logging.Scope;
import com.harmony.umbrella.log.annotation.Module;
import com.harmony.umbrella.log.template.LoggingContext.ValueContext;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class MethodLogRecorder {

    /**
     * 对日志所需要记录的对象序列化的序列化工具
     */
    protected ObjectSerializer serializer;

    /**
     * 日志记录对象的格式化工具
     */
    protected ObjectFormatter formatter;

    /**
     * token resolvers
     */
    protected TokenResolvers tokenResolvers = TokenResolvers.defaultTokenResolvers();

    // 已经被解析过的日志模版缓存
    private Map<Logging, LoggingTemplate> loggingTemplates = new ConcurrentHashMap<>();

    public MethodLogRecorder() {
    }

    public MethodLogRecorder(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    public MethodLogRecorder(ObjectSerializer serializer, ObjectFormatter formatter, TokenResolvers tokenResolvers) {
        this.serializer = serializer;
        this.formatter = formatter;
        this.tokenResolvers = tokenResolvers;
    }

    public LoggingResult aroundProceed(LoggingContext context) throws Exception {
        MethodMonitor monitor = newMethodMonitor(context);
        return monitor.doIt();
    }

    protected MethodMonitor newMethodMonitor(LoggingContext context) {
        Logging loggingAnnotation = getLoggingAnnotation(context);
        return new MethodMonitor(context, loggingAnnotation);
    }

    protected TemplateResolver newTemplateResolver(LoggingTemplate template) {
        return new TemplateResolver(template);
    }

    protected CurrentContext getCurrentContext() {
        return ContextHelper.getCurrentContext();
    }

    protected boolean applyUserContext(LogMessage logMessage) {
        CurrentContext cc = getCurrentContext();
        if (cc != null && cc.getUserId() != null) {
            logMessage.userId(cc.getUserId())//
                    .username(cc.getUsername())//
                    .clientId(cc.getUserHost());
            return true;
        }
        return false;
    }

    protected LoggingTemplate getTemplate(Logging ann) {
        LoggingTemplate template = null;
        if (ann != null) {
            template = loggingTemplates.get(ann);
            if (template == null) {
                template = new LoggingTemplate(ann);
                loggingTemplates.put(ann, template);
            }
        }
        return template;
    }

    public ObjectSerializer getObjectSerializer() {
        return serializer;
    }

    public void setObjectSerializer(ObjectSerializer objectSerializer) {
        this.serializer = objectSerializer;
    }

    public ObjectFormatter getObjectFormatter() {
        return formatter;
    }

    public void setObjectFormatter(ObjectFormatter objectFormatter) {
        this.formatter = objectFormatter;
    }

    public TokenResolvers getTokenResolvers() {
        return tokenResolvers;
    }

    public void setTokenResolvers(TokenResolvers tokenResolvers) {
        this.tokenResolvers = tokenResolvers;
    }

    protected static Logging getLoggingAnnotation(LoggingContext context) {
        Method method = context.getMethod();
        return method.getAnnotation(Logging.class);
    }

    public class MethodMonitor {

        LogMessage logMessage;

        LoggingContext context;
        ValueContext valueContext;

        Logging logging;
        LoggingTemplate loggingTemplate;

        Method method;
        String methodId;
        Object target;
        Object[] arguments;
        Class<?> targetClass;

        Object result;
        Throwable exception;

        private boolean applied;

        public MethodMonitor(LoggingContext context, Logging logging) {
            this.context = context;
            this.target = context.getTarget();
            this.targetClass = target.getClass();
            this.method = context.getMethod();
            this.arguments = context.getArguments();
            this.methodId = StringUtils.getMethodId(context.getMethod());
            this.logging = logging;
            this.loggingTemplate = getTemplate(logging);
            this.valueContext = context.getValueContext();
        }

        public final LoggingResult doIt() throws Exception {
            doPrepare();
            try {
                result = context.proceed();
                doFinish();
            } catch (Throwable e) {
                exception = e;
                doException();
            } finally {
                doLatest();
            }
            return new LoggingResult(result, exception, logMessage);
        }

        // for override

        protected void prepare() {
            logMessage.start()//
                    .module("");
            if (logging != null) {
                logMessage.level(logging.level())//
                        .action(logging.action());
            } else {
                logMessage.level(Level.DEBUG);
            }
        }

        protected void finish() {
            logMessage.result(result);
        }

        protected void exception() {
            logMessage.level(Level.ERROR)//
                    .exception(exception);
        }

        protected void latest() {
            logMessage.finish();
        }

        // default

        private void doPrepare() {
            logMessage = LogMessage//
                    .create(Logs.getLog(targetClass))//
                    .stack(methodId)//
                    .module(getModule())//
                    .currentThread();

            applied = applyUserContext(logMessage);
            if (loggingTemplate != null) {
                ValueContextStack.push(valueContext);
            }
        }

        private void doFinish() {
            finish();
        }

        private void doException() {
            exception();
        }

        private void doLatest() {
            latest();
            if (loggingTemplate != null) {
                ValueContextStack.pop();
            }
            if (!applied) {
                applyUserContext(logMessage);
            }
        }

        private String getModule() {
            String name = null;
            if (logging != null && StringUtils.isNotBlank(logging.module())) {
                name = logging.module();
            } else {
                Module moduleAnn = targetClass.getAnnotation(Module.class);
                if (moduleAnn != null && StringUtils.isNotBlank(moduleAnn.value())) {
                    name = moduleAnn.value();
                }
            }
            if (name == null) {
                name = targetClass.getSimpleName();
            }
            return name;
        }

    }

    public class TemplateResolver {

        private LoggingTemplate loggingTemplate;

        private Map<ScopeToken, Object> resolvedTokens = new HashMap<>();

        public TemplateResolver(LoggingTemplate loggingTemplate) {
            this.loggingTemplate = loggingTemplate;
        }

        public String getMessage(LoggingContext context) {
            StringBuffer o = new StringBuffer();
            ScopeToken[] tokens = loggingTemplate.getFormatedMessageTokens();
            for (ScopeToken token : tokens) {
                if (token.isPlainText()) {
                    o.append(token.getTokenString());
                } else if (resolvedTokens.containsKey(token)) {
                    Object val = resolvedTokens.get(token);
                    o.append(formatter.format(val));
                } else if (tokenResolvers.support(token)) {
                    Object val = tokenResolvers.resolve(token, context);
                    o.append(formatter.format(serializer.serialize(val)));
                } else {
                    o.append("$unresolve");
                }
            }
            return o.toString();
        }

        public String getKey(LoggingContext context) {
            ScopeToken token = loggingTemplate.getKeyToken();
            Object val = resolvedTokens.get(token);
            return val == null ? null : val.toString();
        }

        protected void capture(Scope scope, LoggingContext context) {
            List<ScopeToken> tokens = loggingTemplate.getAllTokens(scope);
            for (ScopeToken token : tokens) {
                if (tokenResolvers.support(token)) {
                    Object val = tokenResolvers.resolve(token, context);
                    resolvedTokens.put(token, serializer.serialize(val));
                }
            }
        }

    }

}
