package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.template.Template;
import com.harmony.umbrella.log.template.TemplateFactory;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.MethodGraphReporter;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingReporter implements MethodGraphReporter {

    private ApplicationContext applicationContext;
    private TemplateFactory templateFactory;

    public LoggingReporter(ApplicationContext applicationContext, TemplateFactory templateFactory) {
        this.applicationContext = applicationContext;
        this.templateFactory = templateFactory;
    }

    @Override
    public void report(MethodGraph graph) {
        report(graph, null);
    }

    @Override
    public void report(MethodGraph graph, HttpServletRequest request) {

        Method method = graph.getMethod();
        Object target = graph.getTarget();

        Logging ann = method.getAnnotation(Logging.class);

        LogMessage logMessage = LogMessage.create(Logs.getLog(target.getClass()));

        if (ann != null) {
            logMessage.bizModule(ann.bizModule())
                    .action(ann.action())
                    .module(ann.module())
                    .level(ann.level());

            Template template = templateFactory.createTemplate(ann, request);
            
            Message message = template.newMessage(graph.getTarget(), graph.getResult(), graph.getParameters());
            Object bizId = template.getId(graph.getTarget(), graph.getResult(), graph.getParameters());
            logMessage.bizId(bizId)
                    .message(message);
        } else {
            logMessage.message(graph.toString());
        }

        if (applicationContext != null && applicationContext.hasCurrentContext()) {
            CurrentContext cc = applicationContext.getCurrentContext();
            logMessage.operatorId(cc.getUserId())
                    .operator(cc.getUsername());
        }

        logMessage.start(graph.getRequestTime())
                .finish(graph.getResponseTime())
                .result(graph.getResult())
                .exception(graph.getThrowable())
                .log();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public TemplateFactory getTemplateFactory() {
        return templateFactory;
    }

    public void setTemplateFactory(TemplateFactory templateFactory) {
        this.templateFactory = templateFactory;
    }
}
