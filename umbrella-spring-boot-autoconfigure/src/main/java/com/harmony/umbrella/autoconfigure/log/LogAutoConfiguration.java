package com.harmony.umbrella.autoconfigure.log;

import org.apache.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.LoggerFactory;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.harmony.umbrella.autoconfigure.log.LogProperties.LoggerType;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.interceptor.LoggingInterceptor;
import com.harmony.umbrella.log.log4j2.LogWriterAppender;
import com.harmony.umbrella.log.support.LogWriter;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnProperty(prefix = "harmony.log", value = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {

    @Autowired(required = false)
    void setLogProvider(LoggerType type) {
        if (type != null) {
            Logs.setLogProvider(type.provider());
        }
    }

    @Configuration
    @ConditionalOnClass({ //
            com.harmony.umbrella.log.interceptor.LoggingInterceptor.class, //
            org.springframework.aop.support.DefaultPointcutAdvisor.class, //
            org.springframework.aop.aspectj.AspectJExpressionPointcut.class//
    })
    @EnableConfigurationProperties(LogInterceptorProperties.class)
    @ConditionalOnProperty(prefix = "harmony.log.interceptor", value = "enabled", havingValue = "true", matchIfMissing = true)
    public static class InterceptorConfiguration {

        private LogInterceptorProperties interceptorProps;

        public InterceptorConfiguration(LogInterceptorProperties interceptorProps) {
            this.interceptorProps = interceptorProps;
        }

        @Bean
        @ConditionalOnProperty(prefix = "harmony.log.interceptor", name = "pointcut")
        PointcutAdvisor loggingAdvisor() {
            LoggingInterceptor advisor = new LoggingInterceptor();
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(interceptorProps.getPointcut());
            advisor.setAnnotationMode(interceptorProps.isAnnotationMode());
            return new DefaultPointcutAdvisor(pointcut, advisor);
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "harmony.log", value = "type", havingValue = "log4j2", matchIfMissing = true)
    @ConditionalOnClass(org.apache.logging.log4j.core.LoggerContext.class)
    public static class Log4J2WriterConfiguration {

        private static final org.slf4j.Logger log = LoggerFactory.getLogger(Log4J2WriterConfiguration.class);

        private LogProperties logProperties;

        public Log4J2WriterConfiguration(LogProperties logProperties) {
            this.logProperties = logProperties;
        }

        @Autowired
        void setRootLoggerLevel() {
            org.apache.logging.log4j.Level level = org.apache.logging.log4j.Level.toLevel(logProperties.getLevel());
            try {
                Configurator.setRootLevel(level);
            } catch (Exception e) {
                log.warn("Unable set log4j2 root level, {}", e.toString());
            }
        }

        @Autowired(required = false)
        @ConditionalOnBean(name = "logWriter")
        void addWriterAppender(@Autowired LogWriter logWriter) {
            setWriterAppender(logWriter, LoggerContext.getContext());
            setWriterAppender(logWriter, LoggerContext.getContext(false));
        }

        private void setWriterAppender(LogWriter writer, LoggerContext ctx) {
            org.apache.logging.log4j.core.config.Configuration cfg = ctx.getConfiguration();
            Logger logger = ctx.getRootLogger();
            cfg.addLoggerAppender(logger, new LogWriterAppender(writer));
            ctx.updateLoggers();
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "harmony.log", value = "type", havingValue = "log4j")
    @ConditionalOnClass(org.apache.log4j.LogManager.class)
    public static class Log4jWriterConfiguration {

        private LogProperties logProperties;

        public Log4jWriterConfiguration(LogProperties logProperties) {
            this.logProperties = logProperties;
        }

        @Autowired
        void setRootLoggerLevel() {
            org.apache.log4j.Level level = org.apache.log4j.Level.toLevel(logProperties.getLevel());
            LogManager.getRootLogger().setLevel(level);
        }

        @Autowired(required = false)
        @ConditionalOnBean(name = "logWriter")
        void addWriterAppender(@Autowired LogWriter logWriter) {
            LogManager.getRootLogger().addAppender(new com.harmony.umbrella.log.log4j.LogWriterAppender(logWriter));
        }

    }

}
