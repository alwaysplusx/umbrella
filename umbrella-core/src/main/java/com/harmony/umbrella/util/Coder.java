package com.harmony.umbrella.util;

import java.io.IOException;

/**
 * 基础编码组件
 * 
 * @author Harmony
 *
 */
public class Coder {

    // FIXME 添加md5, aes, rsa

    /**
     * 将文本进行base64加密
     * 
     * @param key
     *            待加密的文本
     * @return 加密后的文本
     */
    @SuppressWarnings("restriction")
    public static String encryptBASE64(String key) {
        if (Environments.JAVA_17 >= Environments.getMajorJavaVersion()) {
            return new sun.misc.BASE64Encoder().encode(key.getBytes());
        } else {
            return new String(java.util.Base64.getEncoder().encode(key.getBytes()));
        }
    }

    @SuppressWarnings("restriction")
    public static String decryptBASE64(String key) {
        if (Environments.JAVA_17 >= Environments.getMajorJavaVersion()) {
            try {
                return new String(new sun.misc.BASE64Decoder().decodeBuffer(key));
            } catch (IOException e) {
                ReflectionUtils.rethrowRuntimeException(e);
                throw new IllegalStateException(e);
            }
        } else {
            return new String(java.util.Base64.getDecoder().decode(key));
        }
    }

}
