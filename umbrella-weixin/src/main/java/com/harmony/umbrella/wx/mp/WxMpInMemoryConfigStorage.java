package com.harmony.umbrella.wx.mp;

/**
 * @author wuxii
 */
public class WxMpInMemoryConfigStorage extends AbstractWxMpConfigStorage {

    private final WxMpApp wxMpApp;

    private String accessToken;
    private long expiresTime;

    private String jsapiTicket;
    private long jsapiTicketExpiresTime;

    private String cardApiTicket;
    private long cardApiTicketExpiresTime;

    public WxMpInMemoryConfigStorage(WxMpApp wxMpApp, int leadingSeconds) {
        super(leadingSeconds);
        this.wxMpApp = wxMpApp;
    }

    @Override
    protected WxMpApp getWxMpApp() {
        return wxMpApp;
    }

    @Override
    protected void updateWxMpToken(WxMpToken wxMpToken) {
        switch (wxMpToken.getType()) {
            case WxMpToken.ACCESS_TOKEN:
                accessToken = wxMpToken.getToken();
                expiresTime = wxMpToken.getExpiresTime();
                break;
            case WxMpToken.CARDAPI_TICKET:
                cardApiTicket = wxMpToken.getToken();
                cardApiTicketExpiresTime = wxMpToken.getExpiresTime();
                break;
            case WxMpToken.JSAPI_TICKET:
                jsapiTicket = wxMpToken.getToken();
                jsapiTicketExpiresTime = wxMpToken.getExpiresTime();
                break;
        }
    }

    @Override
    protected WxMpToken getWxMpToken(int type) {
        String token = null;
        long expiresTime = 0;
        switch (type) {
            case WxMpToken.ACCESS_TOKEN:
                token = accessToken;
                expiresTime = expiresTime;
                break;
            case WxMpToken.CARDAPI_TICKET:
                token = cardApiTicket;
                expiresTime = cardApiTicketExpiresTime;
                break;
            case WxMpToken.JSAPI_TICKET:
                token = jsapiTicket;
                expiresTime = jsapiTicketExpiresTime;
                break;
        }
        return new WxMpToken(type, token, expiresTime);
    }

}
