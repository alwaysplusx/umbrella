package com.harmony.umbrella.log.template;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.core.ObjectFormatter;
import com.harmony.umbrella.core.ObjectSerializer;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.ProblemHandler;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Logging.Scope;
import com.harmony.umbrella.log.annotation.Module;
import com.harmony.umbrella.log.template.LoggingContext.ValueContext;
import com.harmony.umbrella.util.StringUtils;

/**
 * FIXME 加入可配置选项, 对拦截方法的in/out bound是否进行积极统计
 * 
 * @author wuxii@foxmail.com
 */
public class MethodLogRecorder {

    private static final Log log = Logs.getLog(MethodLogRecorder.class);

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
        this(new JavaObjectSerializer());
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

    protected TemplateResolver newTemplateResolver(Logging loggingAnnotation) {
        return newTemplateResolver(getTemplate(loggingAnnotation));
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
                    .userHost(cc.getUserHost());
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

    protected String getModule(Logging logging, Class<?> targetClass) {
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
        return method != null ? method.getAnnotation(Logging.class) : null;
    }

    public class MethodMonitor {

        LogMessage logMessage;

        LoggingContext context;
        ValueContext valueContext;

        Logging logging;
        TemplateResolver templateResolver;

        Method method;
        String methodId;
        Object target;
        Object[] arguments;
        Class<?> targetClass;

        Object result;
        Throwable exception;

        boolean problemHandled = true;
        boolean userInfoApplied;

        public MethodMonitor(LoggingContext context, Logging logging) {
            this.context = context;
            this.target = context.getTarget();
            this.targetClass = target.getClass();
            this.method = context.getMethod();
            this.arguments = context.getArguments();
            this.methodId = StringUtils.getMethodId(context.getMethod());
            this.logging = logging;
            this.valueContext = context.getValueContext();
            if (logging != null) {
                this.templateResolver = newTemplateResolver(logging);
            }
        }

        public final LoggingResult doIt() throws Exception {
            doPrepare();
            try {
                result = context.proceed();
            } catch (Throwable e) {
                exception = e;
                doException();
            }
            doFinish();
            return new LoggingResult(result, exception, logMessage.asInfo(), problemHandled);
        }

        // for override

        protected void prepare() {
            logMessage.start();
            if (logging != null) {
                logMessage.level(logging.level());
                if (StringUtils.isNotBlank(logging.action())) {
                    logMessage.action(logging.action());
                }
            } else {
                logMessage.level(Level.DEBUG);
            }
        }

        protected void finish() {
            logMessage//
                    .finish()//
                    .result(result);
        }

        protected void exception() {
            logMessage.level(Level.ERROR)//
                    .exception(exception);
        }

        // default

        private void doPrepare() {
            logMessage = LogMessage//
                    .create(Logs.getLog(targetClass))//
                    .stack(methodId)//
                    .module(getModule(logging, targetClass))//
                    .currentThread();
            userInfoApplied = applyUserContext(logMessage);
            if (templateResolver != null) {
                templateResolver.capture(Scope.IN, context);
                ValueContextStack.push(valueContext);
            }
            prepare();
        }

        private void doFinish() {
            finish();
            if (!userInfoApplied) {
                applyUserContext(logMessage);
            }
            if (templateResolver != null) {
                ValueContextStack.pop();
                templateResolver.capture(Scope.OUT, context);
                String message = templateResolver.getMessage(context);
                logMessage.message(message);
            } else {
                logMessage.message("execute method {} input={}, output={}", methodId, Arrays.asList(context.getArguments()), result);
            }

            if (exception != null && !problemHandled && logging != null && logging.handler() != ProblemHandler.class) {
                Class<? extends ProblemHandler> handlerClass = logging.handler();
                try {
                    ProblemHandler handler = handlerClass.newInstance();
                    handler.handle(exception, logMessage.asInfo());
                    problemHandled = true;
                } catch (Throwable e) {
                    log.warn("unhandled method invoke problem", e);
                }
            }
        }

        private void doException() {
            problemHandled = false;
            exception();
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
                String val = null;
                try {
                    if (token.isPlainText()) {
                        val = token.getTokenString();
                    } else if (resolvedTokens.containsKey(token)) {
                        val = formatValue(resolvedTokens.get(token));
                    } else if (tokenResolvers.support(token)) {
                        Object value = tokenResolvers.resolve(token, context);
                        val = formatValue(value);
                    }
                } catch (Exception e) {
                    val = "$unresolve";
                }
                o.append(val);
            }
            return o.toString();
        }

        public String getKey(LoggingContext context) {
            ScopeToken token = loggingTemplate.getKeyToken();
            Object val = resolvedTokens.get(token);
            return val == null ? null : val.toString();
        }

        protected String formatValue(Object val) {
            return val == null ? null : formatter == null ? val.toString() : formatter.format(val);
        }

        protected void capture(Scope scope, LoggingContext context) {
            List<ScopeToken> tokens = loggingTemplate.getAllTokens(scope);
            for (ScopeToken token : tokens) {
                if (tokenResolvers.support(token)) {
                    try {
                        Object val = tokenResolvers.resolve(token, context);
                        resolvedTokens.put(token, serializer.serialize(val));
                    } catch (Throwable e) {
                        // 避免解析中断
                        log.warn("{} unresolved", token, e);
                    }
                }
            }
        }

    }

}
