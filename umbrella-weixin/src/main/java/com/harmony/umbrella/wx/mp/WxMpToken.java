package com.harmony.umbrella.wx.mp;

/**
 * @author wuxii
 */
public class WxMpToken {

    public static final int ACCESS_TOKEN = 1;
    public static final int JSAPI_TICKET = 2;
    public static final int CARDAPI_TICKET = 3;

    public static WxMpToken accessToken(String token, long expiresTime) {
        return new WxMpToken(ACCESS_TOKEN, token, expiresTime);
    }

    public static WxMpToken jsapiTicket(String token, long expiresTime) {
        return new WxMpToken(JSAPI_TICKET, token, expiresTime);
    }

    public static WxMpToken cardApiTicket(String token, long expiresTime) {
        return new WxMpToken(CARDAPI_TICKET, token, expiresTime);
    }

    private int type;
    private String token;
    private long expiresTime;

    WxMpToken(int type, String token, long expiresTime) {
        this.type = type;
        this.token = token;
        this.expiresTime = expiresTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(long expiresTime) {
        this.expiresTime = expiresTime;
    }
}
