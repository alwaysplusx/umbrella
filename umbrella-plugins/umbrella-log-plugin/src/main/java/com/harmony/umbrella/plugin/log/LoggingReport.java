package com.harmony.umbrella.plugin.log;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.MethodGraphReporter;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingReport implements MethodGraphReporter {

    @Override
    public void report(MethodGraph graph) {
        report(graph, null, null);
    }

    @Override
    public void report(MethodGraph graph, HttpServletRequest request, HttpServletResponse response) {
        Method method = graph.getMethod();
        Object target = graph.getTarget();

        LogMessage logMessage = LogMessage.create(Logs.getLog(target.getClass()));
        ValueContext valueContext = wrap(graph, request, response);

        Logging ann = method.getAnnotation(Logging.class);
        if (ann != null) {
            logMessage.bizModule(ann.bizModule())//
                    .action(ann.action())//
                    .module(ann.module())//
                    .level(ann.level());

            LoggingTemplate loggingTemplate = new LoggingTemplate(ann);

            logMessage.bizId(loggingTemplate.getId(valueContext))//
                    .message(loggingTemplate.getMessage(valueContext));
        } else {
            logMessage.message(graph.toString());
        }

        ApplicationContext applicationContext = getApplicationContext();

        if (applicationContext != null && applicationContext.hasCurrentContext()) {
            CurrentContext cc = applicationContext.getCurrentContext();
            logMessage.operatorId(cc.getUserId())//
                    .operator(cc.getUsername());
        }

        logMessage.start(graph.getRequestTime())//
                .finish(graph.getResponseTime())//
                .result(graph.getResult())//
                .exception(graph.getThrowable())//
                .log();
    }

    protected ApplicationContext getApplicationContext() {
        return null;
    }

    protected ValueContext wrap(MethodGraph graph, HttpServletRequest request, HttpServletResponse response) {
        ValueContext context = ValueContext.createDefault();
        context.set("target", graph.getTarget());
        context.set("args", graph.getParameters());
        context.set("result", graph.getResult());
        if (request != null) {
            context.set("request", request);
            context.set("application", request.getServletContext());
            context.set("session", request.getSession());
        }
        if (response != null) {
            context.set("response", response);
        }
        return context;
    }

}
