package com.harmony.umbrella.autoconfigure.web;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.harmony.umbrella.web.method.support.*;
import com.harmony.umbrella.web.servlet.handler.ModelFragmentInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "harmony.web", value = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(WebProperties.class)
@Import(WebAutoConfiguration.WebBundleConfiguration.class)
public class WebAutoConfiguration {

    @ConditionalOnClass({
            BundleParamMethodArgumentResolver.class, BundleModelMethodArgumentResolver.class,
            BundleQueryMethodArgumentResolver.class, BundleViewMethodProcessor.class,
            ModelFragmentInterceptor.class
    })
    public static class WebBundleConfiguration {

        private final WebProperties webProperties;

        public WebBundleConfiguration(WebProperties webProperties) {
            this.webProperties = webProperties;
        }

        @Bean
        BundleViewMethodProcessor bundleViewMethodProcessor() {
            WebProperties.View view = webProperties.getView();
            BundleViewMethodProcessor bundleViewMethodProcessor = new BundleViewMethodProcessor();
            bundleViewMethodProcessor.setKeyStyle(view.getKeyStyle());
            bundleViewMethodProcessor.setDateFormat(view.getDateFormat());
            bundleViewMethodProcessor.setDefaultSerializerFeatures(view.getDefaultSerializerFeatures());
            bundleViewMethodProcessor.getGlobalExcludeFields().addAll(view.getGlobalExcludeFields());
            bundleViewMethodProcessor.getGlobalIncludeFields().addAll(view.getGlobalIncludeFields());
            return bundleViewMethodProcessor;
        }

        @Bean
        WebMvcConfigurer webMvcConfigurer(BundleViewMethodProcessor bundleViewMethodProcessor) {
            return new WebMvcConfigurer() {

                @Override
                public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                    WebProperties.Fastjson fastjson = webProperties.getFastjson();
                    if (fastjson.isEnabled()) {
                        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
                        FastJsonConfig fastJsonConfig = fastJsonHttpMessageConverter.getFastJsonConfig();
                        fastJsonConfig.setCharset(Charset.forName(fastjson.getCharset()));
                        fastJsonConfig.setDateFormat(fastjson.getDateFormat());
                        fastJsonConfig.getSerializeConfig().setPropertyNamingStrategy(fastjson.getPropertyNamingStrategy());
                        fastJsonConfig.setSerializerFeatures(fastjson.getSerializerFeatures().toArray(new SerializerFeature[0]));
                        fastJsonConfig.setWriteContentLength(fastjson.isWriteContentLength());
                        converters.add(fastJsonHttpMessageConverter);
                    }
                }

                @Override
                public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
                    argumentResolvers.add(new BundleParamMethodArgumentResolver());
                    argumentResolvers.add(new BundleModelMethodArgumentResolver());
                    argumentResolvers.add(new BundleQueryMethodArgumentResolver());
                    argumentResolvers.add(bundleViewMethodProcessor);
                }

                @Override
                public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
                    returnValueHandlers.add(bundleViewMethodProcessor);
                }

                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new ModelFragmentInterceptor());
                }

            };
        }

    }

}
