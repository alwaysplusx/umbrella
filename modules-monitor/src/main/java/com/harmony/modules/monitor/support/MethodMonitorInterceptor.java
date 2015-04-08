/*
 * Copyright 2013-2015 wuxii@foxmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.modules.monitor.support;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.monitor.AbstractGraph;
import com.harmony.modules.monitor.MethodMonitor;
import com.harmony.modules.monitor.util.MonitorUtils;

/**
 * @author wuxii@foxmail.com
 */
@Interceptor
@MethodMonitorInterceptor.Monitored
public class MethodMonitorInterceptor implements MethodMonitor {

    protected static final Logger log = LoggerFactory.getLogger(MethodMonitorInterceptor.class);
    /**
     * 受到监视的资源
     */
    private Map<String, Object> monitorList = new HashMap<String, Object>();
    /**
     * 是否开启白名单策略，开启后只拦截在监视名单中的资源
     */
    private boolean useWhiteList;

    /**
     * 监听入口
     * @param ctx 执行的环境上下文
     * @return 调用ctx.process()返回
     * @throws Exception
     */
    @AroundInvoke
    public Object monitor(InvocationContext ctx) throws Exception {
        Method method = ctx.getMethod();
        if (isMonitored(method)) {
            Object result = null;
            log.debug("interceptor method [{}] of [{}]", method, ctx.getTarget());
            MethodGraphImpl graph = new MethodGraphImpl(ctx.getTarget(), method, ctx.getParameters());
            try {
                result = ctx.proceed();
                graph.setResult(result);
            } catch (Exception e) {
                graph.setException(e);
                throw e;
            } finally {
                graph.setResponseTime(Calendar.getInstance());
                try {
                    persistGraph(graph);
                } catch (Exception e) {
                    log.debug("", e);
                }
            }
            return result;
        }
        return ctx.proceed();
    }

    /**
     * 保存方法监视结果
     * @param graph
     */
    protected void persistGraph(MethodGraph graph) {
        // TODO 保存监视结果
    }

    /**
     * 将方转化为唯一的资源限定表示
     * @param method
     * @return
     */
    protected String methodIdentifie(Method method) {
        return MonitorUtils.methodIdentifie(method);
    }

    @Override
    public void exclude(String resource) {
        monitorList.remove(resource);
    }

    @Override
    public void include(String resource) {
        monitorList.put(resource, null);
    }

    @Override
    public String[] getMonitorList() {
        Set<String> set = monitorList.keySet();
        return set.toArray(new String[set.size()]);
    }

    @Override
    public void exclude(Method method) {
        exclude(methodIdentifie(method));
    }

    @Override
    public void include(Method method) {
        include(methodIdentifie(method));
    }

    @Override
    public boolean isUseWhiteList() {
        return useWhiteList;
    }

    @Override
    public void useWhiteList(boolean use) {
        this.useWhiteList = use;
    }

    public void setWhiteList(boolean whiteList) {
        this.useWhiteList = whiteList;
    }

    @Override
    public boolean isMonitored(String resource) {
        if (useWhiteList) {
            return monitorList.containsKey(resource);
        }
        return true;
    }

    @Override
    public boolean isMonitored(Method method) {
        return isMonitored(methodIdentifie(method));
    }

    /**
     * 此注解配合CDI使用. /META-INF/beans.xml
     */
    @Inherited
    @InterceptorBinding
    @Target({ TYPE, METHOD })
    @Retention(RUNTIME)
    public @interface Monitored {

        String module() default "";

        String operator() default "";

    }

    public class MethodGraphImpl extends AbstractGraph implements MethodGraph {

        private Object target;
        private Method method;
        private Object[] args;

        public MethodGraphImpl() {
        }

        public MethodGraphImpl(Object target, Method method, Object[] args) {
            this.target = target;
            this.method = method;
            this.args = args;
        }

        public Object getTarget() {
            return target;
        }

        @Override
        public Map<String, Object> getArguments() {
            Map<String, Object> arguments = new HashMap<String, Object>();
            if (args != null) {
                for (int i = 0, max = args.length; i < max; i++) {
                    arguments.put(i + 1 + "", args[i]);
                }
            }
            return arguments;
        }

        @Override
        @Deprecated
        public void setArguments(Map<String, Object> arguments) {
            super.setArguments(arguments);
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object[] getArgs() {
            return args;
        }

        @Override
        public String getModule() {
            Monitored ann = method.getAnnotation(Monitored.class);
            if (ann != null) {
                return ann.module();
            }
            return null;
        }

        @Override
        public String getOperator() {
            Monitored ann = method.getAnnotation(Monitored.class);
            if (ann != null) {
                return ann.operator();
            }
            return null;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

    }
}
