package com.harmony.umbrella.cache;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;

/**
 * @author wuxii@foxmail.com
 */
public class J2CacheTest {

    public static void main(String[] args) {
        CacheChannel channel = J2Cache.getChannel();
        System.out.println(channel);
    }

}
