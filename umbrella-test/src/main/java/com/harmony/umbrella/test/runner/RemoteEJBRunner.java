package com.harmony.umbrella.test.runner;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.ee.EJBApplicationContextProvider;
import com.harmony.umbrella.ee.EJBBeanFactory;
import com.harmony.umbrella.test.ContainerConfiguration;
import com.harmony.umbrella.test.TestUtils;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
@Deprecated
public class RemoteEJBRunner extends BlockJUnit4ClassRunner {

    private EJBBeanFactory beanFactory;

    public RemoteEJBRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        Statement next = super.withBeforeClasses(statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Class<?> testClass = getTestClass().getJavaClass();
                ContainerConfiguration ann = testClass.getAnnotation(ContainerConfiguration.class);
                Properties properties = new Properties();
                if (ann != null) {
                    if (StringUtils.isNotBlank(ann.location().trim())) {
                        properties.putAll(PropertiesUtils.loadProperties(ann.location().trim()));
                    }
                    properties.putAll(TestUtils.toProperties(ann.properties()));

                    Class<? extends InitialContextFactory> initialContextFactoryClass = ann.initialContextFactory();
                    if (initialContextFactoryClass != InitialContextFactory.class) {
                        properties.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryClass.getName());
                    }

                    if (StringUtils.isNotBlank(ann.providerUrl())) {
                        properties.put(Context.PROVIDER_URL, ann.providerUrl());
                    }
                }
                new EJBApplicationContextProvider().createApplicationContext();
                next.evaluate();
            }
        };
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        Statement next = super.withBefores(method, target, statement);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                TestClass testClass = getTestClass();
                List<FrameworkField> fields = testClass.getAnnotatedFields(EJB.class);
                for (FrameworkField field : fields) {
                    if (field.get(target) != null) {
                        continue;
                    }
                    Field realField = field.getField();
                    Object bean = null;
                    String jndi = getJndi(realField);
                    if (StringUtils.isNotBlank(jndi)) {
                        try {
                            bean = beanFactory.lookup(jndi);
                        } catch (BeansException e) {
                            EJB ejbAnnotation = realField.getAnnotation(EJB.class);
                            if (ejbAnnotation != null) {
                                bean = beanFactory.lookup(field.getType(), ejbAnnotation);
                            } else {
                                bean = beanFactory.lookup(field.getType());
                            }
                        }
                    }

                    if (bean == null) {
                        throw new NoSuchBeanFoundException(field.getName());
                    }

                    ReflectionUtils.setFieldValue(realField, target, bean);
                }
                next.evaluate();
            }
        };
    }

    private String getJndi(Field field) {
        EJB ann = field.getAnnotation(EJB.class);
        if (StringUtils.isNotBlank(ann.lookup())) {
            return ann.lookup();
        }
        return null;
    }
}
