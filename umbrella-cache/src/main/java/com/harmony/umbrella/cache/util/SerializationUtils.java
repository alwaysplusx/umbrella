package com.harmony.umbrella.cache.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.CacheException;

/**
 * 对象序列化工具包
 *
 * @author winterlau
 */
public class SerializationUtils {

    private final static Logger log = LoggerFactory.getLogger(SerializationUtils.class);
    private static Serializer g_ser;

    static {
        String ser = "";
        if (ser == null || "".equals(ser.trim()))
            g_ser = new JavaSerializer();
        else {
            if (ser.equals("java")) {
                g_ser = new JavaSerializer();
            } else {
                try {
                    g_ser = (Serializer) Class.forName(ser).newInstance();
                } catch (Exception e) {
                    throw new CacheException("Cannot initialize Serializer named [" + ser + ']', e);
                }
            }
        }
        log.info("Using Serializer -> [" + g_ser.name() + ":" + g_ser.getClass().getName() + ']');
    }

    public static byte[] serialize(Object obj) throws IOException {
        return g_ser.serialize(obj);
    }

    public static Object deserialize(byte[] bytes) throws IOException {
        return g_ser.deserialize(bytes);
    }

}
