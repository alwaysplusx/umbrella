package com.harmony.umbrella.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 基础编码组件
 * 
 * @author Harmony
 *
 */
public class Coder {

    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";

    /**
     * 转换成十六进制值
     * 
     * @param bytes
     *            待转化的字节数组
     * @return 十六进制文本
     */
    public static String byte2Hex(byte[] bytes) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        int bit;
        for (int i = 0; i < bytes.length; i++) {
            bit = (bytes[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bytes[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }

    /**
     * 十六进制转换字符串
     * 
     * @param hex
     *            String 十六进制
     * @return String 转换后的字符串
     */
    public static byte[] hex2Byte(String hex) {
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return bytes;
    }

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

    /**
     * 将文本进行BASE64解密
     * 
     * @param key
     *            待解密的密文
     * @return 解密后的明文
     */
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

    /**
     * MD5加密
     * 
     * @param text
     *            文本明文
     * @param encoding
     *            文本的字符集
     * @return 加密后密文
     * @throws Exception
     */
    public static String encryptMD5(String text, String encoding) throws Exception {
        return encryptMD5(text.getBytes(encoding));
    }

    /**
     * MD5加密
     * 
     * @param data
     *            待加密的字节数组
     * @return 加密后的密文文本
     * @throws Exception
     */
    public static String encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data);
        return byte2Hex(md5.digest());
    }

    /**
     * SHA加密明文
     * 
     * @param text
     *            明文
     * @param encoding
     *            文本字符集
     * @return SHA后的密文
     * @throws Exception
     */
    public static String encryptSHA(String text, String encoding) throws Exception {
        return new String(encryptSHA(text.getBytes(encoding)));
    }

    /**
     * SHA加密
     * 
     * @param data
     *            待加密的字节数组
     * @return
     * @throws Exception
     */
    public static byte[] encryptSHA(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);
        return sha.digest();
    }

    /**
     * aes加密
     * 
     * @param data
     *            待加密的字节数组
     * @param key
     *            加密密钥
     * @return 加密后的密文
     * @throws Exception
     */
    public static String encryptAES(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return byte2Hex(cipher.doFinal(data));
    }

    /**
     * aes解密
     * 
     * @param data
     *            待解密的字节数组
     * @param key
     *            解密密钥
     * @return 解密后的明文
     * @throws Exception
     */
    public static String decryptAES(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return byte2Hex(cipher.doFinal(data));
    }

    public static byte[] aesKey(String key, int length) {
        return Arrays.copyOf(key.getBytes(), length);
    }
}
