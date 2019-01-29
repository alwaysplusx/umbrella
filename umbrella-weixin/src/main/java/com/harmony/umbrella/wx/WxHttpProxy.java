package com.harmony.umbrella.wx;

import lombok.*;

/**
 * @author wuxii
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxHttpProxy {

    private int port;
    private String host;
    private String username;
    private String password;

}
