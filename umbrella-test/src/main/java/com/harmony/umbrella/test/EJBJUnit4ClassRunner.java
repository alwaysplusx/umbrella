package com.harmony.umbrella.test;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.harmony.umbrella.context.ee.EJBBeanFactory;
import com.harmony.umbrella.context.ee.EJBBeanFactoryImpl;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class EJBJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    private EJBBeanFactory beanFactory;

    public EJBJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
        try {
            this.beanFactory = createEJBBeanFactory(klass);
        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }

    private EJBBeanFactory createEJBBeanFactory(Class<?> klass) throws IOException {

        ContainerConfiguration ann = klass.getAnnotation(ContainerConfiguration.class);
        Properties properties = new Properties();

        if (ann != null) {
            properties.putAll(PropertiesUtils.loadProperties(ann.location()));

            ContainerConfiguration.ActiveProperty[] activeProperties = ann.properties();
            for (ContainerConfiguration.ActiveProperty ap : activeProperties) {
                properties.put(ap.name(), ap.value());
            }

            Class<? extends InitialContextFactory> initialContextFactoryClass = ann.initialContextFactory();
            if (initialContextFactoryClass != InitialContextFactory.class) {
                properties.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryClass.getName());
            }

            if (StringUtils.isNotBlank(ann.providerUrl())) {
                properties.put(Context.PROVIDER_URL, ann.providerUrl());
            }
        }

        return EJBBeanFactoryImpl.create(properties);

    }

    @SuppressWarnings("deprecation")
    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        Statement junitBefores = super.withBefores(method, target, statement);
        return new EJBRunBefores(junitBefores, target, getTestClass(), beanFactory);
    }

}
