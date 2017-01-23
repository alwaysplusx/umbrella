package com.harmony.umbrella.test.runner;

import java.lang.reflect.Field;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.harmony.umbrella.test.ContainerConfiguration;
import com.harmony.umbrella.test.Naming;
import com.harmony.umbrella.test.OpenEJBConfiguration;
import com.harmony.umbrella.test.TestUtils;
import com.harmony.umbrella.util.PropertiesUtils;

/**
 * @author wuxii@foxmail.com
 */
public class OpenEJBRunner extends BlockJUnit4ClassRunner {

    private EJBContainer container;

    public OpenEJBRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Class<?> testClass = getTestClass().getJavaClass();
                Properties properties = new Properties();
                ContainerConfiguration ann = testClass.getAnnotation(ContainerConfiguration.class);
                if (ann != null) {
                    if (ann.location().trim().equals("")) {
                        properties.putAll(PropertiesUtils.loadProperties(ann.location().trim()));
                    }
                    properties.putAll(TestUtils.toProperties(ann.properties()));
                }
                OpenEJBConfiguration openejbAnn = testClass.getAnnotation(OpenEJBConfiguration.class);
                if (openejbAnn != null) {
                    properties.putAll(TestUtils.toProperties(openejbAnn.properties()));
                    if (openejbAnn.openejb().trim().equals("")) {
                        properties.put("openejb.conf.file", openejbAnn.openejb().trim());
                    }
                }
                // OpenEjbContainer(L324)
                properties.put(org.apache.openejb.OpenEjbContainer.Provider.OPENEJB_ADDITIONNAL_CALLERS_KEY, testClass.getName());
                container = EJBContainer.createEJBContainer(properties);
                statement.evaluate();
            }
        };
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        Statement next = super.withBefores(method, target, statement);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                Context context = container.getContext();
                context.bind("inject", target);
                Field[] fields = target.getClass().getDeclaredFields();
                for (Field field : fields) {
                    Naming ann = field.getAnnotation(Naming.class);
                    if (ann != null) {
                        Object obj = null;
                        if (!ann.value().trim().equals("")) {
                            obj = context.lookup(ann.value().trim());
                        } else {
                            obj = context;
                        }
                        field.setAccessible(true);
                        field.set(target, obj);
                    }
                }
                next.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfterClasses(final Statement statement) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                statement.evaluate();
                container.close();
            }
        };
    }

}