package com.harmony.umbrella.autoconfigure.wx;

import com.harmony.umbrella.wx.WxHttpProxy;
import com.harmony.umbrella.wx.mp.WxMpApp;
import com.harmony.umbrella.wx.mp.WxMpInMemoryConfigStorage;
import com.harmony.umbrella.wx.mp.WxMpInRedisConfigStorage;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author wuxii
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnClass({WxMpService.class, WxMpApp.class})
@EnableConfigurationProperties(WxMpProperties.class)
@ConditionalOnProperty(prefix = "weixin.mp", name = {"id", "secret"})
public class WxMpAutoConfiguration {

    @Bean
    @ConditionalOnBean(WxMpConfigStorage.class)
    @ConditionalOnMissingBean(name = "wxMpService")
    WxMpService wxMpService(WxMpConfigStorage wxMpConfigStorage) {
        WxMpServiceImpl service = new WxMpServiceImpl();
        service.setWxMpConfigStorage(wxMpConfigStorage);
        return service;
    }

    @Configuration
    @ConditionalOnMissingBean(WxMpConfigStorage.class)
    @Import({WxMpConfigStorageConfiguration.Memory.class, WxMpConfigStorageConfiguration.Redis.class})
    static class WxMpConfigStorageConfiguration {

        @Order(1)
        @ConditionalOnBean(StringRedisTemplate.class)
        @ConditionalOnMissingBean(WxMpConfigStorage.class)
        static class Redis {

            @Bean
            WxMpConfigStorage wxMpInRedisConfigStorage(WxMpProperties wxMpProperties, StringRedisTemplate stringRedisTemplate) {
                WxMpApp wxMpApp = buildWxMpApp(wxMpProperties);
                WxHttpProxy httpProxy = buildWxHttpProxy(wxMpProperties.getProxy());
                WxMpProperties.Redis redis = wxMpProperties.getRedis();
                WxMpInRedisConfigStorage storage = new WxMpInRedisConfigStorage(wxMpApp,
                        redis.getPrefix(),
                        wxMpProperties.getLeadingSeconds(),
                        stringRedisTemplate
                );
                storage.setHttpProxy(httpProxy);
                if (redis.isClearFirst()) {
                    storage.clear();
                }
                return storage;
            }

        }

        @Order(2)
        @ConditionalOnMissingBean(WxMpConfigStorage.class)
        static class Memory {

            @Bean
            WxMpConfigStorage wxMpInMemoryConfigStorage(WxMpProperties wxMpProperties) {
                WxMpApp wxMpApp = buildWxMpApp(wxMpProperties);
                WxHttpProxy httpProxy = buildWxHttpProxy(wxMpProperties.getProxy());
                int leadingSeconds = wxMpProperties.getLeadingSeconds();
                WxMpInMemoryConfigStorage storage = new WxMpInMemoryConfigStorage(wxMpApp, leadingSeconds);
                storage.setHttpProxy(httpProxy);
                return storage;
            }

        }

    }

    private static WxHttpProxy buildWxHttpProxy(WxMpProperties.Proxy proxy) {
        if (proxy == null) {
            return new WxHttpProxy();
        }
        return WxHttpProxy.builder()
                .host(proxy.getHost())
                .port(proxy.getPort())
                .username(proxy.getUsername())
                .password(proxy.getPassword())
                .build();
    }

    private static WxMpApp buildWxMpApp(WxMpProperties wxMpProps) {
        return WxMpApp.builder()
                .appId(wxMpProps.getId())
                .secret(wxMpProps.getSecret())
                .aesKey(wxMpProps.getAesKey())
                .autoRefreshToken(wxMpProps.isAutoRefreshToken())
                .redirectUri(wxMpProps.getRedirectUri())
                .templateId(wxMpProps.getTemplateId())
                .token(wxMpProps.getToken())
                .build();
    }

}
