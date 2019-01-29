package com.harmony.umbrella.wx.mp;

import lombok.*;

/**
 * @author wuxii
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WxMpApp {

    private String appId;
    private String secret;
    private String token;
    private String redirectUri;
    private String aesKey;
    private String templateId;
    private boolean autoRefreshToken;

}
