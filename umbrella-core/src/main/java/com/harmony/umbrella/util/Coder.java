package com.harmony.umbrella.util;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 基础编码组件
 * 
 * @author Harmony
 *
 */
@SuppressWarnings("restriction")
public class Coder {

    // FIXME 添加md5, aes, rsa

    /**
     * 将文本进行base64加密
     * 
     * @param key
     *            待加密的文本
     * @return 加密后的文本
     */
    public static String encryptBASE64(String key) {
        return new BASE64Encoder().encode(key.getBytes());
    }

    public static String decryptBASE64(String key) throws IOException {
        return new String(new BASE64Decoder().decodeBuffer(key));
    }

    /**
     * BASE64加密
     * 
     * @param key
     *            待加密的字节数组
     * @return 加密后的字节数据组
     * @throws IOException
     */
    /*
    public static byte[] encryptBASE64(byte[] key) throws IOException {
     return new BASE64Encoder().encode(key).getBytes("UTF-8");
    }

    public static byte[] decryptBASE64(byte[] key) throws IOException {
     return new BASE64Decoder().decodeBuffer(new ByteArrayInputStream(key));
    }*/
}
