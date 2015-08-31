/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.ws.jaxws;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.Handler;
import com.harmony.umbrella.ws.Handler.HandleMethod;
import com.harmony.umbrella.ws.Phase;
import com.harmony.umbrella.ws.WebServiceAbortException;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;
import com.harmony.umbrella.ws.support.SimpleContext;
import com.harmony.umbrella.ws.visitor.AbstractPhaseVisitor;
import com.harmony.umbrella.ws.visitor.PhaseValidationVisitor;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsExecutorAndPhaseValTest {

    private static final Logger log = LoggerFactory.getLogger(JaxWsExecutorAndPhaseValTest.class);
    private static final String address = "http://localhost:8081/hello";
    private static final JaxWsExecutor executor = new JaxWsCXFExecutor();

    private static int count = 0;

    @BeforeClass
    public static void setUp() {
        // 发布HelloWebService服务在地址http://localhost:8081/hello上
        JaxWsServerBuilder.create().publish(HelloWebService.class, address);
    }

    @Test
    public void testLoggerVisitor() {
        SimpleContext context = new SimpleContext(HelloService.class, "sayHi", new Object[] { "wuxii" });
        context.setAddress(address);
        executor.execute(context, new AbstractPhaseVisitor() {
            private static final long serialVersionUID = 1L;

            @Override
            public void visitFinally(Object result, Throwable throwable, Context context) {
                log.info("{}", context.get(JaxWsExecutor.WS_CONTEXT_GRAPH));
            }

        });
    }

    @Test
    public void testHelloServicePhaseVal() {
        // 设置接口调用的上下文(服务接口, 服务方法名, 服务的参数)
        SimpleContext context = new SimpleContext(HelloService.class, "sayHi", new Object[] { "wuxii" });
        // 设置服务的所在地址
        context.setAddress(address);
        // 调用执行者执行方法，
        // PhaseValidationVisitor用于帮助加载HelloServiceSayHiPhaseValidation实例进行接口的执行周期检验
        Object result = executor.execute(context, new PhaseValidationVisitor());
        // 对结果进行断言判断
        assertNotNull(result);
        assertEquals("Hi wuxii", result);
        assertEquals(2, count);
    }

    /**
     * 接口客户端的周期检验类 ps:该类拦截了{@linkplain HelloService#sayHi(String)}方法 将调用分解为
     * 前-(取消)-后-异常
     *
     * @author wuxii@foxmail.com
     */
    // @Handler注解表明这个类拦截接口HelloService的执行
    @Handler(HelloService.class)
    public static class HelloServiceSayHiPhaseValidation {

        /**
         * 在调用接口{@linkplain HelloService#sayHi(String)}前拦截调用
         *
         * @param message
         *            客户端情求时候的参数
         * @param content
         *            上下文中用户设置的内容
         * @return true表示交互可以继续执行， false交互将被终止
         */
        // Phase.PRE_INVOKE 表明执行前调用
        @HandleMethod(phase = Phase.PRE_INVOKE)
        public boolean sayHi(String message, Map<String, Object> content) {
            count++;
            return true;
        }

        /**
         * 在调用被取消时候调用
         *
         * @param exception
         *            取消异常
         * @param message
         *            客户端情求时候的参数
         * @param content
         *            上下文中用户设置的内容
         */
        @HandleMethod(phase = Phase.ABORT)
        public void sayHi(WebServiceAbortException exception, String message, Map<String, Object> content) {
        }

        /**
         * 在调用成功时候被调用
         *
         * @param result
         *            接口返回的结果
         * @param message
         *            请求的参数
         * @param content
         *            用户设置的上下文内容
         */
        @HandleMethod(phase = Phase.POST_INVOKE)
        public void sayHi(String result, String message, Map<String, Object> content) {
            count++;
        }

        /**
         * 在调用异常时候被调用
         *
         * @param e
         *            异常信息
         * @param message
         *            请求参数
         * @param content
         *            用户设置的上下文
         */
        @HandleMethod(phase = Phase.THROWING)
        public void sayHi(Throwable e, String message, Map<String, Object> content) {
        }

    }

    public static void main(String[] args) {
        JaxWsServerBuilder.create().publish(HelloWebService.class, address);
    }

}
