package com.harmony.umbrella.ee;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationContextProvider;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * {@linkplain ApplicationContextProvider}EJB环境支持
 *
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContextProvider implements ApplicationContextProvider {

    private static final String DEFAULT_CONTEXT = "classpath*:default-ee.xml";

    private static final String APPLICATION_CONTEXT = "classpath:huiju-ee.xml";

    private static org.springframework.context.ApplicationContext springContext;

    private static final Log log = Logs.getLog(EJBApplicationContextProvider.class);

    @Override
    public ApplicationContext createApplicationContext() {
        if (springContext == null) {
            synchronized (EJBApplicationContextProvider.class) {
                if (springContext == null) {
                    org.springframework.context.ApplicationContext app = getSpringApplicationContext();
                    springContext = app;
                }
            }
        }
        return new EJBApplicationContext(springContext);
    }

    private org.springframework.context.ApplicationContext getSpringApplicationContext() {
        org.springframework.context.ApplicationContext app = new ClassPathXmlApplicationContext(DEFAULT_CONTEXT);

        ServerMetadata serverMetadata = ApplicationContext.getServerMetadata();
        if (serverMetadata.serverType != ServerMetadata.UNKNOW) {
            try {
                String fileLocation = "classpath*:default-" + serverMetadata.serverName + ".xml";
                Resource[] resources = app.getResources(fileLocation);
                if (resources != null && resources.length > 0) {
                    // wrapper with container application context
                    log.info("{} config with {}", serverMetadata.serverName, Arrays.asList(resources));
                    app = new ClassPathXmlApplicationContext(new String[] { fileLocation }, app);
                } else {
                    log.info("{} no specified application configuration", serverMetadata.serverName);
                }
            } catch (IOException e) {
                log.info("{} no specified application configuration", serverMetadata.serverName, e);
            }
        }

        Resource appResource = app.getResource(APPLICATION_CONTEXT);
        if (appResource.exists()) {
            app = new ClassPathXmlApplicationContext(new String[] { APPLICATION_CONTEXT }, app);
        } else {
            log.info("You can add huiju-ee.xml in classpath to override default application configuration");
        }
        return app;
    }

}
