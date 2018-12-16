package com.harmony.umbrella.wx.mp;

/**
 * @author wuxii
 */
public class WxMpException extends RuntimeException {

    public WxMpException() {
        super();
    }

    public WxMpException(String message) {
        super(message);
    }

    public WxMpException(String message, Throwable cause) {
        super(message, cause);
    }

    public WxMpException(Throwable cause) {
        super(cause);
    }

}
