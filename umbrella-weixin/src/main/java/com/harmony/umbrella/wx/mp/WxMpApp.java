package com.harmony.umbrella.wx.mp;

import lombok.Builder;

/**
 * @author wuxii
 */
@Builder
public class WxMpApp {

    private String appId;
    private String secret;
    private String token;
    private String redirectUri;
    private String aesKey;
    private String templateId;
    private boolean autoRefreshToken;

    public WxMpApp(String appId, String secret, String token, String redirectUri, String aesKey, String templateId,
                   boolean autoRefreshToken) {
        this.appId = appId;
        this.secret = secret;
        this.token = token;
        this.redirectUri = redirectUri;
        this.aesKey = aesKey;
        this.templateId = templateId;
        this.autoRefreshToken = autoRefreshToken;
    }

    public String getAppId() {
        return appId;
    }

    public String getSecret() {
        return secret;
    }

    public String getToken() {
        return token;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getAesKey() {
        return aesKey;
    }

    public String getTemplateId() {
        return templateId;
    }

    public boolean isAutoRefreshToken() {
        return autoRefreshToken;
    }

    void setAppId(String appId) {
        this.appId = appId;
    }

    void setSecret(String secret) {
        this.secret = secret;
    }

    void setToken(String token) {
        this.token = token;
    }

    void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    void setAutoRefreshToken(boolean autoRefreshToken) {
        this.autoRefreshToken = autoRefreshToken;
    }
}
