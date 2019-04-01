package com.harmony.umbrella.lock;

import org.junit.Test;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author wuxii
 */
public class SpELTest {

    @Test
    public void testMethodBaseSpEL() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Foo foo = (Foo) Proxy.newProxyInstance(loader, new Class[]{Foo.class}, new FooInvocationHandler(new FooImpl()));
        foo.greeting("foo");
    }

    interface Foo {
        String greeting(String name);
    }

    static class FooImpl implements Foo {
        @Override
        public String greeting(String name) {
            return "hi " + name;
        }
    }

    static class FooEvaluationContext extends MethodBasedEvaluationContext {
        public FooEvaluationContext(Object rootObject, Method method, Object[] arguments) {
            super(rootObject, method, arguments, new DefaultParameterNameDiscoverer());
        }
    }

    static class FooInvocationHandler implements InvocationHandler {

        private Foo foo;
        private SpelExpressionParser parser = new SpelExpressionParser();

        public FooInvocationHandler(Foo foo) {
            this.foo = foo;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            FooExpressionRootObject rootObject = new FooExpressionRootObject(method, args, foo);
            FooEvaluationContext evaluationContext = new FooEvaluationContext(rootObject, method, args);
            Object value = parser.parseExpression("user-handle:#p0").getValue(evaluationContext);
            System.out.println(value);
            return method.invoke(foo, args);
        }

    }

    static class FooExpressionRootObject {

        Method method;
        Object[] args;
        Object target;
        Class<?> targetClass;

        public FooExpressionRootObject(Method method, Object[] args, Object target) {
            this.method = method;
            this.args = args;
            this.target = target;
            this.targetClass = target.getClass();
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }

        public Object getTarget() {
            return target;
        }

        public Class<?> getTargetClass() {
            return targetClass;
        }
    }

}
