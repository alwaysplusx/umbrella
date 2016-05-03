package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;

import javax.interceptor.Interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.template.Holder;
import com.harmony.umbrella.log.template.TemplateMessageFactory;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.support.AbstractEJBMonitorInterceptor;
import com.harmony.umbrella.monitor.support.InvocationContext;

@Interceptor
public class EJBLoggingInterceptor extends AbstractEJBMonitorInterceptor {

    @Override
    protected Object doInterceptor(InvocationContext invocationContext) throws Exception {
        Object result = invocationContext.process();
        logging(invocationContext.toGraph());
        return result;
    }

    protected void logging(MethodGraph graph) {

        Method method = graph.getMethod();
        Object target = graph.getTarget();

        LogMessage logMessage = LogMessage.create(Logs.getLog(target.getClass()));

        Logging ann = method.getAnnotation(Logging.class);
        if (ann != null) {
            logMessage.bizModule(ann.bizModule())
                    .action(ann.action())
                    .module(ann.module())
                    .level(ann.level());
            
            Holder holder = wrap(graph);
            TemplateMessageFactory templateMessageFactory = new TemplateMessageFactory(ann);

            logMessage.bizId(templateMessageFactory.getId(holder))
                    .message(templateMessageFactory.newMessage(holder));
        } else {
            logMessage.message(graph.toString());
        }

        ApplicationContext applicationContext = getApplicationContext();
        
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

    protected Holder wrap(MethodGraph graph) {
        return new Holder(graph.getTarget(), graph.getResult(), graph.getParameters(), getRequest(), getResponse());
    }
    
    protected HttpServletRequest getRequest() {
        return null;
    }

    protected HttpServletResponse getResponse() {
        return null;
    }

    protected ApplicationContext getApplicationContext() {
        return ApplicationContext.getApplicationContext();
    }
}
