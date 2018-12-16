package com.harmony.umbrella.wx.mp;

import com.harmony.umbrella.util.FileUtils;
import com.harmony.umbrella.wx.WxHttpProxy;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wuxii
 */
public abstract class AbstractWxMpConfigStorage implements WxMpConfigStorage {

    protected static final int DEFAULT_LEADING_SECONDS = 60;

    protected final Lock accessTokenLock = new ReentrantLock();
    protected final Lock jsapiTicketLock = new ReentrantLock();
    protected final Lock cardApiTicketLock = new ReentrantLock();

    protected final int leadingSeconds;

    protected WxHttpProxy httpProxy;

    protected AbstractWxMpConfigStorage(int leadingSeconds) {
        this.leadingSeconds = leadingSeconds;
    }

    protected abstract WxMpApp getWxMpApp();

    protected abstract void updateWxMpToken(WxMpToken wxMpToken);

    protected abstract WxMpToken getWxMpToken(int type);

    protected WxHttpProxy getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(WxHttpProxy httpProxy) {
        this.httpProxy = httpProxy;
    }

    protected String getTmpDirectory() {
        return null;
    }

    protected WxMpToken getWxMpAccessToken() {
        return getWxMpToken(WxMpToken.ACCESS_TOKEN);
    }

    protected WxMpToken getWxMpJsapiTicket() {
        return getWxMpToken(WxMpToken.JSAPI_TICKET);
    }

    protected WxMpToken getWxMpCardApiTicket() {
        return getWxMpToken(WxMpToken.CARDAPI_TICKET);
    }

    protected boolean isExpired(long expiresTime) {
        return System.currentTimeMillis() > expiresTime;
    }

    protected long expiresTime(int expiresInSeconds) {
        return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiresInSeconds - leadingSeconds);
    }

    @Override
    public String getAccessToken() {
        return getWxMpAccessToken().getToken();
    }

    @Override
    public Lock getAccessTokenLock() {
        return accessTokenLock;
    }

    @Override
    public boolean isAccessTokenExpired() {
        return isExpired(getWxMpAccessToken().getExpiresTime());
    }

    @Override
    public void expireAccessToken() {
        updateAccessToken(null, 0);
    }

    @Override
    public void updateAccessToken(WxAccessToken accessToken) {
        updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresInSeconds) {
        updateWxMpToken(WxMpToken.accessToken(accessToken, expiresTime(expiresInSeconds)));
    }

    @Override
    public String getJsapiTicket() {
        return getWxMpJsapiTicket().getToken();
    }

    @Override
    public Lock getJsapiTicketLock() {
        return jsapiTicketLock;
    }

    @Override
    public boolean isJsapiTicketExpired() {
        return isExpired(getWxMpJsapiTicket().getExpiresTime());
    }

    @Override
    public void expireJsapiTicket() {
        updateJsapiTicket(null, 0);
    }

    @Override
    public void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        updateWxMpToken(WxMpToken.jsapiTicket(jsapiTicket, expiresTime(expiresInSeconds)));
    }

    @Override
    public String getCardApiTicket() {
        return getWxMpCardApiTicket().getToken();
    }

    @Override
    public Lock getCardApiTicketLock() {
        return cardApiTicketLock;
    }

    @Override
    public boolean isCardApiTicketExpired() {
        return isExpired(getWxMpCardApiTicket().getExpiresTime());
    }

    @Override
    public void expireCardApiTicket() {
        updateCardApiTicket(null, 0);
    }

    @Override
    public void updateCardApiTicket(String cardApiTicket, int expiresInSeconds) {
        updateWxMpToken(WxMpToken.cardApiTicket(cardApiTicket, expiresTime(expiresInSeconds)));
    }

    @Override
    public String getAppId() {
        return getWxMpApp().getAppId();
    }

    @Override
    public String getSecret() {
        return getWxMpApp().getSecret();
    }

    @Override
    public String getToken() {
        return getWxMpApp().getToken();
    }

    @Override
    public String getAesKey() {
        return getWxMpApp().getAesKey();
    }

    @Override
    public String getTemplateId() {
        return getWxMpApp().getTemplateId();
    }

    @Override
    public long getExpiresTime() {
        return getWxMpAccessToken().getExpiresTime();
    }

    @Override
    public String getOauth2redirectUri() {
        return getWxMpApp().getRedirectUri();
    }

    @Override
    public boolean autoRefreshToken() {
        return getWxMpApp().isAutoRefreshToken();
    }

    @Override
    public String getHttpProxyHost() {
        WxHttpProxy proxy = getHttpProxy();
        return proxy == null ? null : proxy.getHost();
    }

    @Override
    public int getHttpProxyPort() {
        WxHttpProxy proxy = getHttpProxy();
        return proxy == null ? 0 : proxy.getPort();
    }

    @Override
    public String getHttpProxyUsername() {
        WxHttpProxy proxy = getHttpProxy();
        return proxy == null ? null : proxy.getUsername();
    }

    @Override
    public String getHttpProxyPassword() {
        WxHttpProxy proxy = getHttpProxy();
        return proxy == null ? null : proxy.getPassword();
    }

    @Override
    public File getTmpDirFile() {
        String dirString = getTmpDirectory();
        try {
            return dirString == null ? null : FileUtils.createDir(dirString);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public ApacheHttpClientBuilder getApacheHttpClientBuilder() {
        return null;
    }

}
