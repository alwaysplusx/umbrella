//package com.harmony.umbrella.plugin.log;
//
//import java.lang.reflect.Method;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.harmony.umbrella.context.ApplicationContext;
//import com.harmony.umbrella.context.CurrentContext;
//import com.harmony.umbrella.core.MethodGraph;
//import com.harmony.umbrella.core.MethodGraphReport;
//import com.harmony.umbrella.log.LogMessage;
//import com.harmony.umbrella.log.Logs;
//import com.harmony.umbrella.log.annotation.Logging;
//import com.harmony.umbrella.util.StringUtils;
//
///**
// * @author wuxii@foxmail.com
// */
//public class LoggingReport implements MethodGraphReport {
//
//    @Override
//    public void report(MethodGraph graph) {
//        report(graph, null, null);
//    }
//
//    @Override
//    public void report(MethodGraph graph, HttpServletRequest request, HttpServletResponse response) {
//        Method method = graph.getMethod();
//        Object target = graph.getTarget();
//
//        LogMessage logMessage = LogMessage.create(Logs.getLog(target.getClass()));
//        ValueContext valueContext = wrap(graph, request, response);
//
//        Logging ann = getLogging(method, target);
//
//        if (ann != null) {
//            logMessage.action(ann.action())//
//                    .module(ann.module())//
//                    .type(ann.type())//
//                    .level(ann.level());
//
//            LoggingTemplate loggingTemplate = new LoggingTemplate(ann);
//
//            logMessage.key(loggingTemplate.getKey(valueContext))//
//                    .message(loggingTemplate.getMessage(valueContext));
//        } else {
//            logMessage.message(graph.toString());
//        }
//
//        ApplicationContext applicationContext = getApplicationContext();
//
//        if (applicationContext != null && applicationContext.hasCurrentContext()) {
//            CurrentContext cc = applicationContext.getCurrentContext();
//            logMessage.operatorId(cc.getUserId())//
//                    .operatorName(cc.getUsername());
//        }
//
//        logMessage.stack(StringUtils.getMethodId(method))//
//                .threadName(Thread.currentThread().getName())//
//                .start(graph.getRequestTime())//
//                .finish(graph.getResponseTime())//
//                .result(graph.getResult())//
//                .exception(graph.getThrowable())//
//                .log();
//    }
//
//    protected Logging getLogging(Method method, Object target) {
//        Logging ann = method.getAnnotation(Logging.class);
//        if (ann == null && target != null && method.getDeclaringClass() != target.getClass()) {
//            try {
//                Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
//                ann = targetMethod.getAnnotation(Logging.class);
//            } catch (Exception e) {
//            }
//        }
//        return ann;
//    }
//
//    protected ApplicationContext getApplicationContext() {
//        return null;
//    }
//
//    protected ValueContext wrap(MethodGraph graph, HttpServletRequest request, HttpServletResponse response) {
//        ValueContext context = ValueContext.createDefault();
//        context.set("target", graph.getTarget());
//        Object[] args = graph.getParameters();
//        context.set("args", args);
//        for (int i = 0; i < args.length; i++) {
//            context.set(i + "", args[i]);
//        }
//        context.set("result", graph.getResult());
//        if (request != null) {
//            context.set("request", request);
//            context.set("application", request.getServletContext());
//            context.set("session", request.getSession());
//        }
//        if (response != null) {
//            context.set("response", response);
//        }
//        return context;
//    }
//
//}
