package com.harmony.umbrella.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.JavaMetadata;
import com.harmony.umbrella.context.metadata.OperatingSystemMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.web.WebApplicationInitializer;

/**
 * 在web应用启动时,伴随开始初始化应用程序
 * 
 * @author wuxii@foxmail.com
 */
public class WebApplicationContextInitializer implements WebApplicationInitializer {

    private static final Log log = Logs.getLog(WebApplicationContextInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        ApplicationContext.initStatic(new ApplicationConfiguration().withServletContext(servletContext));

        if (log.isDebugEnabled()) {
            showApplicationInfo();
        }
    }

    private void showApplicationInfo() {

        StringBuilder out = new StringBuilder();

        ApplicationConfiguration cfg = ApplicationContext.getApplicationConfiguration();
        DatabaseMetadata databaseMetadata = ApplicationContext.getDatabaseMetadata();
        ServerMetadata serverMetadata = ApplicationContext.getServerMetadata();
        JavaMetadata javaMetadata = ApplicationMetadata.getJavaMetadata();
        OperatingSystemMetadata osMetadata = ApplicationMetadata.getOperatingSystemMetadata();

        out//
                .append("\n################################################################")//
                .append("\n#                     Application Information                  #")//
                .append("\n################################################################")//
                .append("\n#                   cpu : ").append(osMetadata.cpu)//
                .append("\n#               os name : ").append(osMetadata.osName)//
                .append("\n#             time zone : ").append(osMetadata.timeZone)//
                .append("\n#            os version : ").append(osMetadata.osVersion)//
                .append("\n#             user home : ").append(osMetadata.userHome)//
                .append("\n#         file encoding : ").append(osMetadata.fileEncoding)//
                .append("\n#")//
                .append("\n#              jvm name : ").append(javaMetadata.vmName)//
                .append("\n#            jvm vendor : ").append(javaMetadata.vmVendor)//
                .append("\n#           jvm version : ").append(javaMetadata.vmVersion)//
                .append("\n#")//
                .append("\n#              database : ").append(databaseMetadata.productName)//
                .append("\n#          database url : ").append(databaseMetadata.url)//
                .append("\n#         database user : ").append(databaseMetadata.userName)//
                .append("\n#           driver name : ").append(databaseMetadata.driverName)//
                .append("\n#        driver version : ").append(databaseMetadata.driverVersion)//
                .append("\n#")//
                .append("\n#            app server : ").append(serverMetadata.serverName)//
                .append("\n#       servlet version : ").append(serverMetadata.servletVersion)//
                .append("\n#")//
                .append("\n#             spec name : ").append(javaMetadata.specificationName)//
                .append("\n#          spec version : ").append(javaMetadata.specificationVersion)//
                .append("\n#             java home : ").append(javaMetadata.javaHome)//
                .append("\n#           java vendor : ").append(javaMetadata.javaVendor)//
                .append("\n#          java version : ").append(javaMetadata.javaVersion)//
                .append("\n#       runtime version : ").append(javaMetadata.runtimeVersion)//
                .append("\n#  application packages : ").append(cfg != null ? cfg.getPackages() : null)//
                .append("\n################################################################")//
                .append("\n\n");
    }

}