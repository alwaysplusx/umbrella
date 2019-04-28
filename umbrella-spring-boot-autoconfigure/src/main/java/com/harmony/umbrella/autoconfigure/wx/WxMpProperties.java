package com.harmony.umbrella.autoconfigure.wx;

import com.harmony.umbrella.wx.mp.WxMpInRedisConfigStorage;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuxii
 */
@ConfigurationProperties(prefix = "weixin.mp")
public class WxMpProperties {

    private String id;
    private String secret;
    private String token;
    private String redirectUri;
    private String aesKey;
    private String templateId;
    private boolean autoRefreshToken;

    private String tempDirFile;
    private int leadingSeconds;

    private Redis redis = new Redis();
    private Proxy proxy;
    private HttpClient httpClient;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public boolean isAutoRefreshToken() {
        return autoRefreshToken;
    }

    public void setAutoRefreshToken(boolean autoRefreshToken) {
        this.autoRefreshToken = autoRefreshToken;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static class Proxy {

        private String username;
        private String host;
        private String password;
        private int port = 0;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class Redis {

        private String prefix = WxMpInRedisConfigStorage.DEFAULT_REDIS_KEY_PREFIX;
        private boolean clearFirst;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public boolean isClearFirst() {
            return clearFirst;
        }

        public void setClearFirst(boolean clearFirst) {
            this.clearFirst = clearFirst;
        }

    }

    public static class HttpClient {

    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public String getTempDirFile() {
        return tempDirFile;
    }

    public void setTempDirFile(String tempDirFile) {
        this.tempDirFile = tempDirFile;
    }

    public int getLeadingSeconds() {
        return leadingSeconds;
    }

    public void setLeadingSeconds(int leadingSeconds) {
        this.leadingSeconds = leadingSeconds;
    }
}
