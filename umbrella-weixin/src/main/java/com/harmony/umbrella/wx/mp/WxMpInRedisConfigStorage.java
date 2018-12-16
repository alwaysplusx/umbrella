package com.harmony.umbrella.wx.mp;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author wuxii
 */
public class WxMpInRedisConfigStorage extends AbstractWxMpConfigStorage {

    public static final String DEFAULT_REDIS_KEY_PREFIX = "weixin:mp:tokens:";

    private final StringRedisTemplate stringRedisTemplate;
    private final WxMpApp wxMpApp;
    private final String redisKey;

    public WxMpInRedisConfigStorage(WxMpApp wxMpApp, StringRedisTemplate stringRedisTemplate) {
        this(wxMpApp, DEFAULT_REDIS_KEY_PREFIX, stringRedisTemplate);
    }

    public WxMpInRedisConfigStorage(WxMpApp wxMpApp, String prefix, StringRedisTemplate stringRedisTemplate) {
        this(wxMpApp, prefix, DEFAULT_LEADING_SECONDS, stringRedisTemplate);
    }

    public WxMpInRedisConfigStorage(WxMpApp wxMpApp, String prefix, int leadingSeconds, StringRedisTemplate stringRedisTemplate) {
        super(leadingSeconds);
        this.wxMpApp = wxMpApp;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisKey = prefix + wxMpApp.getAppId();
    }

    public void clear() {
        stringRedisTemplate.delete(redisKey);
    }

    @Override
    protected WxMpApp getWxMpApp() {
        return wxMpApp;
    }

    @Override
    protected void updateWxMpToken(WxMpToken wxMpToken) {
        switch (wxMpToken.getType()) {
            case WxMpToken.ACCESS_TOKEN:
                setToRedis("accessToken", wxMpToken.getToken());
                setToRedis("expiresTime", wxMpToken.getExpiresTime());
                break;
            case WxMpToken.CARDAPI_TICKET:
                setToRedis("cardApiTicket", wxMpToken.getToken());
                setToRedis("cardApiTicketExpiresTime", wxMpToken.getExpiresTime());
                break;
            case WxMpToken.JSAPI_TICKET:
                setToRedis("jsapiTicket", wxMpToken.getToken());
                setToRedis("jsapiTicketExpiresTime", wxMpToken.getExpiresTime());
                break;
            default:
                throw new WxMpException("unsupport weixin mp token type " + wxMpToken.getType());
        }
    }

    @Override
    protected WxMpToken getWxMpToken(int type) {
        String token = null;
        long expiresTime = 0;
        switch (type) {
            case WxMpToken.ACCESS_TOKEN:
                token = getFromRedis("accessToken");
                expiresTime = Long.valueOf(getFromRedis("expiresTime", "0"));
                break;
            case WxMpToken.CARDAPI_TICKET:
                token = getFromRedis("cardApiTicket");
                expiresTime = Long.valueOf(getFromRedis("cardApiTicketExpiresTime", "0"));
                break;
            case WxMpToken.JSAPI_TICKET:
                token = getFromRedis("jsapiTicket");
                expiresTime = Long.valueOf(getFromRedis("jsapiTicketExpiresTime", "0"));
                break;
            default:
                throw new WxMpException("unsupport weixin mp token type " + type);
        }
        return new WxMpToken(type, token, expiresTime);
    }

    protected final void setToRedis(String key, Object val) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        if (val == null) {
            hashOperations.delete(this.redisKey, key);
        } else {
            hashOperations.put(this.redisKey, key, val.toString());
        }
    }

    protected final String getFromRedis(String key) {
        return getFromRedis(key, null);
    }

    protected final String getFromRedis(String key, String def) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String val = hashOperations.get(this.redisKey, key);
        return val == null ? def : val;
    }

}
