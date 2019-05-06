package com.harmony.umbrella.autoconfigure.log;

import com.harmony.umbrella.log.interceptor.LogInterceptor;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "harmony.log", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({LogProperties.class})
@Import({LogAutoConfiguration.InterceptorConfiguration.class})
public class LogAutoConfiguration {

    @ConditionalOnClass({
            LogInterceptor.class,
            org.springframework.aop.support.DefaultPointcutAdvisor.class,
            org.springframework.aop.aspectj.AspectJExpressionPointcut.class
    })
    @ConditionalOnProperty(prefix = "harmony.log.interceptor", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class InterceptorConfiguration {

        private final LogProperties logProperties;

        public InterceptorConfiguration(LogProperties logProperties) {
            this.logProperties = logProperties;
        }

        @Bean
        @ConditionalOnProperty(prefix = "harmony.log.interceptor", name = "pointcut")
        PointcutAdvisor loggingAdvisor(LogInterceptor logInterceptor) {
            LogProperties.Interceptor interceptorProps = logProperties.getInterceptor();
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(interceptorProps.getPointcut());
            return new DefaultPointcutAdvisor(pointcut, logInterceptor);
        }

        @Bean
        @ConditionalOnMissingBean(LogInterceptor.class)
        LogInterceptor logInterceptor() {
            return new LogInterceptor();
        }

    }

}