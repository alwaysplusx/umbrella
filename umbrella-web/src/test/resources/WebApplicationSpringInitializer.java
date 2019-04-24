import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.ApplicationServletContainerInitializer;
import com.harmony.umbrella.context.WebXmlConstant;

/**
 * @author wuxii@foxmail.com
 */
public class WebApplicationSpringInitializer extends ApplicationServletContainerInitializer implements WebApplicationInitializer {

    private ApplicationConfiguration appConfig;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (appConfig != null) {
            servletContext.setAttribute(WebXmlConstant.CONTAINER_CONTEXT_ATTRIBUTE_APP_CONFIG, appConfig);
        }
        this.onStartup(null, servletContext);
    }

    public ApplicationConfiguration getApplicationConfiguration() {
        return appConfig;
    }

    public void setApplicationConfiguration(ApplicationConfiguration appConfig) {
        this.appConfig = appConfig;
    }

}
