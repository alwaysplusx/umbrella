package com.harmony.umbrella.autoconfigure.log;

import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.StaticLogger;
import com.harmony.umbrella.log.interceptor.LoggingInterceptor;
import com.harmony.umbrella.log.log4j2.LogWriterAppender;
import com.harmony.umbrella.log.spi.Log4j2LogProvider;
import com.harmony.umbrella.log.spi.Log4jLogProvider;
import com.harmony.umbrella.log.support.LogWriter;
import org.apache.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnProperty(prefix = "harmony.log", value = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({LogProperties.class})
@Import({LogAutoConfiguration.InterceptorConfiguration.class, LogAutoConfiguration.LogConfiguration.class})
public class LogAutoConfiguration {

    @ConditionalOnClass({
            com.harmony.umbrella.log.interceptor.LoggingInterceptor.class,
            org.springframework.aop.support.DefaultPointcutAdvisor.class,
            org.springframework.aop.aspectj.AspectJExpressionPointcut.class
    })
    @ConditionalOnProperty(prefix = "harmony.log.interceptor", value = "enabled", havingValue = "true", matchIfMissing = true)
    static class InterceptorConfiguration {

        private final LogProperties logProperties;

        public InterceptorConfiguration(LogProperties logProperties) {
            this.logProperties = logProperties;
        }

        @Bean
        @ConditionalOnProperty(prefix = "harmony.log.interceptor", name = "pointcut")
        PointcutAdvisor loggingAdvisor() {
            LogProperties.Interceptor interceptorProps = logProperties.getInterceptor();
            LoggingInterceptor advisor = new LoggingInterceptor();
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(interceptorProps.getPointcut());
            advisor.setAnnotationMode(interceptorProps.isAnnotationMode());
            return new DefaultPointcutAdvisor(pointcut, advisor);
        }

    }

    @Configuration
    @Import({LogConfiguration.Log4j2.class, LogConfiguration.Log4j.class})
    static class LogConfiguration {

        @ConditionalOnProperty(prefix = "harmony.log", value = "type", havingValue = "log4j2", matchIfMissing = true)
        @ConditionalOnClass(org.apache.logging.log4j.core.LoggerContext.class)
        static class Log4j2 {

            private LogProperties logProperties;

            public Log4j2(LogProperties logProperties) {
                this.logProperties = logProperties;
            }

            @Bean
            CommandLineRunner log4j2InitRunner(@Autowired(required = false) LogWriter logWriter) {
                return args -> {
                    org.apache.logging.log4j.Level level = org.apache.logging.log4j.Level.toLevel(logProperties.getLevel());
                    try {
                        Configurator.setRootLevel(level);
                    } catch (Exception e) {
                        StaticLogger.warn("Unable set log4j2 root level.", e);
                    }

                    Logs.setLogProvider(new Log4jLogProvider());

                    try {
                        if (logWriter != null) {
                            setWriterAppender(logWriter, LoggerContext.getContext());
                            setWriterAppender(logWriter, LoggerContext.getContext(false));
                        }
                    } catch (Exception e) {
                        StaticLogger.warn("Unable set log4j2 logWriter.", e);
                    }

                };
            }

            private void setWriterAppender(LogWriter writer, LoggerContext ctx) {
                org.apache.logging.log4j.core.config.Configuration cfg = ctx.getConfiguration();
                Logger logger = ctx.getRootLogger();
                cfg.addLoggerAppender(logger, new LogWriterAppender(writer));
                ctx.updateLoggers();
            }

        }

        @ConditionalOnProperty(prefix = "harmony.log", value = "type", havingValue = "log4j", matchIfMissing = true)
        @ConditionalOnClass(org.apache.log4j.LogManager.class)
        static class Log4j {

            private LogProperties logProperties;

            public Log4j(LogProperties logProperties) {
                this.logProperties = logProperties;
            }

            @Bean
            CommandLineRunner log4jInitRunner() {
                return args -> {
                    try {
                        org.apache.log4j.Level level = org.apache.log4j.Level.toLevel(logProperties.getLevel());
                        LogManager.getRootLogger().setLevel(level);
                    } catch (Exception e) {
                        StaticLogger.warn("Unable set log4j level.", e);
                    }

                    Logs.setLogProvider(new Log4j2LogProvider());
                };
            }

        }

    }


}
